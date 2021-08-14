package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;

    private static final int PORT = 8189;

    private List<ClientHandler> clients = new CopyOnWriteArrayList<>();//  // список подключившихся клиентов, храним в арайлисте, он потокобезопасный;
    private AuthService authService = new SimpleAuthService();

    public Server() {
        try {
            server = new ServerSocket(PORT);// запускаем
            System.out.println("Server started!");

            while (true) {
                this.socket = this.server.accept();// сервер ждет пока к нему кто нибудь подключится
                // если кто то подключился, создается сокет
                System.out.println("Client connected");
                socket.setSoTimeout(5000);
                // если один эндом вышел, нужно что бы он удалился из списка (методы подписаться и отписаться)
                new ClientHandler(this.socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                server.close();// в блоке файнали сервер нужно закрыть
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void broadcastMsg(ClientHandler sender, String msg) {

        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        Iterator var4 = this.clients.iterator();

        while (var4.hasNext()) {

            ClientHandler c = (ClientHandler) var4.next();
            c.sendMsg(message);

        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] to [ %s ] : %s", sender.getNickname(), receiver, msg);
        Iterator var5 = this.clients.iterator();

        ClientHandler c;
        do {
            if (!var5.hasNext()) {
                sender.sendMsg("Not found user: " + receiver);
                return;
            }

            c = (ClientHandler) var5.next();
        } while (!c.getNickname().equals(receiver));

        c.sendMsg(message);
        if (!c.equals(sender)) {
            sender.sendMsg(message);
        }

    }

    public boolean isLoginAuthenticated(String login) {
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)) {
                return true;
            }
        }
        return false;

    }
      /*  Iterator var2 = this.clients.iterator();

        ClientHandler c;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            c = (ClientHandler)var2.next();
        } while(!c.getLogin().equals(login));

        return true;*/
    // }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist");
        Iterator var2 = this.clients.iterator();

        while (var2.hasNext()) {
            ClientHandler c = (ClientHandler) var2.next();
            sb.append(" ").append(c.getNickname());
        }

        String message = sb.toString();
        Iterator var6 = this.clients.iterator();

        while (var6.hasNext()) {
            ClientHandler c = (ClientHandler) var6.next();
            c.sendMsg(message);
        }

    }

    public void subscribe(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
        this.broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
        this.broadcastClientList();
    }

    public AuthService getAuthService() {
        return this.authService;
    }
}

