package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Server {
    private CopyOnWriteArrayList<ClientHandler> clients;
    private AuthService authService;
    private static final Logger logger = Logger.getLogger("");

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(new FileInputStream("log.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        authService = new SimpleAuthService();
        if (!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к БД");
        }
        authService = new DBAuthServise();

        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8189);
            //System.out.println("Сервер запущен");
            logger.log(Level.INFO, "Сервер запущен");
            while (true) {
                socket = server.accept();
                //System.out.println("Клиент подключился");
                logger.log(Level.INFO, "Клиент подключился");
                new ClientHandler(this, socket, logger);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SQLHandler.disconnect();
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
        logger.log(Level.FINE,  nick + " отправил сообщение в чат");
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s",
                sender.getNick(), receiver, msg);

        for (ClientHandler c : clients) {
            if (c.getNick().equals(receiver)) {
                c.sendMsg(message);
                sender.sendMsg(message);
                logger.log(Level.FINE, c.getNick() + " отправил приватное сообщение");
                return;
            }
        }
        sender.sendMsg("Пользователь с ником: " + receiver + " не найден");
        logger.log(Level.WARNING, "Ошибка отправки сообщения: пользователь не найден");
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
