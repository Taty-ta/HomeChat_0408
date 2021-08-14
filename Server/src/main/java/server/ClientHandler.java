//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientHandler {
    Socket socket;
    Server server;
    DataInputStream in;
    DataOutputStream out;
    private boolean authenticated;
    private String nickname;
    private String login;

    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            (new Thread(() -> {
                try {

                    String str;
                    String[] token;
                    // аунтификация
                    while (true) {
                        str = this.in.readUTF();
                        if (str.equals("/end")) {
                            this.sendMsg("/end");
                            System.out.println("Client disconnected");
                            break;
                        }
                        //проверка логина на вхождение
                        if (str.startsWith("/auth ")) {
                            token = str.split("\\s+");
                            try {
                            this.nickname = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);
                            this.login = token[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("Ввели только логин или только пароль");
                            }
                            if (this.nickname != null) {
                                if (!server.isLoginAuthenticated(this.login)) {
                                    this.sendMsg("/authok " + this.nickname);
                                    server.subscribe(this);
                                    this.authenticated = true;
                                    socket.setSoTimeout(0);// аунтиф прошли, что бы нас не выбрасывало
                                    break;
                                }

                                this.sendMsg("С этим логином уже вошли");
                            } else {
                                this.sendMsg("Неверный логин / пароль");
                            }
                        }

/*
                        if (str.startsWith("/reg ")) {
                            token = str.split("\\s+");
                            if (token.length >= 4) {
                                boolean regOk = server.getAuthService().registration(token[1], token[2], token[3]);
                                if (regOk) {
                                    this.sendMsg("/regok");
                                } else {
                                    this.sendMsg("/regno");
                                }
                            }
                        }
*/
                        if (str.startsWith("/reg")) {
                            token = str.split("\\s+");
                            if (token.length < 4) {
                                continue;
                            }
                            boolean regOk = server.getAuthService().registration(token[1], token[2], token[3]);
                            if (regOk) {
                                this.sendMsg("/regok");
                            } else {
                                this.sendMsg("/regno");
                            }
                        }

                    }
// цикл работы
                    while (this.authenticated) {
                        str = this.in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                this.sendMsg("/end");
                                System.out.println("Client disconnected");
                                break;
                            }

                            if (str.startsWith("/w")) {
                                token = str.split("\\s+", 3);
                                if (token.length >= 3) {
                                    server.privateMsg(this, token[1], token[2]);
                                }
                            }
                        } else {
                            server.broadcastMsg(this, str);
                        }
                    }

                } catch (SocketTimeoutException e) {
                    sendMsg("/end");

                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    server.unsubscribe(this);

                    try {
                        socket.close();
                    } catch (IOException var13) {
                        var13.printStackTrace();
                    }

                }

            })).start();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void sendMsg(String msg) {
        try {
            this.out.writeUTF(msg);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public String getNickname() {
        return this.nickname;
    }

    public String getLogin() {
        return this.login;
    }
}
