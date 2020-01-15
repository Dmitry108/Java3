package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private Server server;
    private Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private String nick;
    private String login;

    private static final Object monitor = new Object();
    private static ExecutorService service = Executors.newCachedThreadPool();

    public String getLogin() {
        return login;
    }

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        System.out.println(Thread.currentThread().getName());
        try {
            this.server = server;
            this.socket = socket;
            //

//            System.out.println("LocalPort: "+socket.getLocalPort());
//            System.out.println("Port: "+socket.getPort());
//            System.out.println("InetAddress: "+socket.getInetAddress());
            System.out.println("RemoteSocketAddress: " + socket.getRemoteSocketAddress());

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            service.execute(() -> {
            //new Thread(()->{
                try {
                    //цикл авторизации
                    //установка лимита на молчание по сокету.
                    socket.setSoTimeout(120000);
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            sendMsg("/end");
                            throw new RuntimeException("отключаемся");
                        }
                        if (str.startsWith("/reg ")) {
                            String[] token = str.split(" ");
                            synchronized (monitor){
                                boolean b = server.getAuthService()
                                        .registration(token[1], token[2], token[3]);
                                if (!b) {
                                    sendMsg("Ошибка: с этим логином уже Зарегистированы.");
                                } else {
                                    sendMsg("Регистрация прошла успешно.");
                                }
                            }
                        }
                        if (str.startsWith("/auth ")) {
                            synchronized (monitor){
                                String[] token = str.split(" ");
                                String newNick = server.getAuthService()
                                        .getNicknameByLoginAndPassword(token[1], token[2]);
                                if (newNick != null) {
                                    if (!server.isLoginAuthorized(token[1])) {
                                        login = token[1];
                                        sendMsg("/authok " + newNick);
                                        nick = newNick;
                                        server.subscribe(this);
                                        System.out.println("Клиент " + nick + " подключился");
                                        socket.setSoTimeout(0);
                                        break;
                                    } else {
                                        sendMsg("С этим логином уже авторизовались");
                                    }
                                } else {
                                    sendMsg("Неверный логин / пароль");
                                }
                            }
                        }
                    }
                    // цикл работы
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                break;
                            }
                            if (str.startsWith("/w ")) {
                                String[] token = str.split(" ", 3);
                                server.privateMsg(this, token[1], token[2]);
                            }
                            if (str.startsWith("/chnick ")) {
                                synchronized (monitor){
                                    String[] token = str.split(" ", 2);
                                    if (token[1].contains(" ")) {
                                        sendMsg("Ник не может содержать пробелов");
                                        continue;
                                    }
                                    if (server.getAuthService().changeNick(this.nick, token[1])) {
                                        sendMsg("/yournickis " + token[1]);
                                        sendMsg("Ваш ник изменен на " + token[1]);
                                        this.nick = token[1];
                                        server.broadcastClientlist();
                                    } else {
                                        sendMsg("Не удалось изменить ник. Ник " + token[1] + " уже существует");
                                    }
                                }
                            }
                        } else {
                            server.broadcastMsg(nick, str);
                            System.out.println("письмо из "+Thread.currentThread().getName());
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println("bue");
                } catch (SocketTimeoutException e) {
                    sendMsg("/end");
                    System.out.println("bue time out");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
                    System.out.println("Клиент отключился");
                }
            });//.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void finalize(){
        service.shutdown();
    }
}