//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


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

                    // аунтификация
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            sendMsg("/end");
                            System.out.println("Client disconnected");
                            break;
                        }
                        //проверка логина на вхождение
                        if (str.startsWith("/auth ")) {
                            String[] token = str.split("\\s+");
                            nickname = server.getAuthService()
                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                            login = token[1];
                            if (nickname != null) {
                                if (!server.isLoginAuthenticated(login)) {
                                    sendMsg("/authok " + nickname);
                                    server.subscribe(this);
                                    authenticated = true;
                                    //==============//
                                    //    sendMsg(SQLHandler.getMessageForNick(nickname));
                                    //==============//
                                    break;
                                } else {
                                    sendMsg("С этим логином уже вошли");
                                }
                            } else {
                                sendMsg("Неверный логин / пароль");
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
                            String[] token = str.split("\\s+");
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
// цикл работы // цикл работы
                    // цикл работы
                    while (authenticated) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                System.out.println("Client disconnected");
                                break;
                            }

                            if (str.startsWith("/w")) {
                                String[] token = str.split("\\s+", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMsg(this, token[1], token[2]);
                            }

                            //==============//
                            if (str.startsWith("/chnick ")) {
                                String[] token = str.split("\\s+", 2);
                                if (token.length < 2) {
                                    continue;
                                }
                                if (token[1].contains(" ")) {
                                    sendMsg("Ник не может содержать пробелов");
                                    continue;
                                }
                                if (server.getAuthService().changeNick(this.nickname, token[1])) {
                                    sendMsg("/yournickis " + token[1]);
                                    sendMsg("Ваш ник изменен на " + token[1]);
                                    this.nickname = token[1];
                                    server.broadcastClientList();
                                } else {
                                    sendMsg("Не удалось изменить ник. Ник " + token[1] + " уже существует");
                                }
                            }
                            //==============//
                        } else {
                            server.broadcastMsg(this, str);
                        }
                    }

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
