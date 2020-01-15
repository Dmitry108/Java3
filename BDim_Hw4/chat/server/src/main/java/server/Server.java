package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Vector<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
//        authService = new SimpleAuthService();
        if (!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к БД");
        }
        authService = new DBAuthServise();

        ServerSocket server = null;
        //Socket socket = null;
        //ExecutorService executorService = Executors.newCachedThreadPool();//newFixedThreadPool(10);

        try {
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while (true) {
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                //executorService.execute(()->{
                    new ClientHandler(this, socket);
                //});
                // сначала попытался применить пул потоков при создании нового экземпляра ClientHandler'а,
                // но, в последствии понял, что реализация на текущем этапе безсмысленна,
                // поскольку исполнение создаваемого потока ограничено
                // созданием сокета клиента, входного и выходного потоков, а также созданием нового потока исполнения,
                // где реализована вся логика взаимодействия сервера с клиентом.
                // Таким образом, для каждого клиента итак создается новый поток.
                // Поэтому, решил применить ExecuterService в классе ClientHandler, сделав его статической переменной, общей для всего класса,
                // помещая в него каждый создаваемый поток для нового подключенного клиента.
                // Смысл применения ExecuterService, думаю, имеется. Так как, тестируя приложение, заметил,
                // что в ранней версии при каждом новом подключении создается новый поток, даже если кто-то уже отключился и поток закончился,
                // а в данном подходе задействуются неиспользующиеся потоки.
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SQLHandler.disconnect();
            //executorService.shutdown();
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String nick, String msg) {
        for (ClientHandler c : clients) {
            c.sendMsg(nick + " : " + msg);
        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s",
                sender.getNick(), receiver, msg);

        for (ClientHandler c : clients) {
            if (c.getNick().equals(receiver)) {
                c.sendMsg(message);
                sender.sendMsg(message);
                return;
            }
        }
        sender.sendMsg("Пользователь с ником: " + receiver + " не найден");
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientlist();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientlist();
    }

    public boolean isLoginAuthorized(String login) {
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastClientlist() {
        StringBuilder sb = new StringBuilder("/clientlist ");
        for (ClientHandler c : clients) {
            sb.append(c.getNick() + " ");
        }

        String msg = sb.toString();
        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }
}