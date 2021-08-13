//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private List<SimpleAuthService.UserData> users = new ArrayList();

    public SimpleAuthService() {
        this.users.add(new SimpleAuthService.UserData("qwe", "qwe", "qwe"));
        this.users.add(new SimpleAuthService.UserData("asd", "asd", "asd"));
        this.users.add(new SimpleAuthService.UserData("zxc", "zxc", "zxc"));

        for(int i = 1; i < 10; ++i) {
            this.users.add(new SimpleAuthService.UserData("login" + i, "pass" + i, "nick" + i));
        }

    }

    public String getNicknameByLoginAndPassword(String login, String password) {
        Iterator var3 = this.users.iterator();

        SimpleAuthService.UserData u;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            u = (SimpleAuthService.UserData)var3.next();
        } while(!u.login.equals(login) || !u.password.equals(password));

        return u.nickname;
    }
 // проверка на регистрацию из интерфейса
    public boolean registration(String login, String password, String nickname) {
        for (UserData u : users) {
           if (u.login.equals(login) || u.nickname.equals(nickname)) {
               return false;
            }
        }
        users.add(new UserData(login, password, nickname));
        return true;

        /*Iterator var4 = this.users.iterator();

        SimpleAuthService.UserData u;
        do {
            if (!var4.hasNext()) {
                this.users.add(new SimpleAuthService.UserData(login, password, nickname));
                return true;
            }

            u = (SimpleAuthService.UserData)var4.next();
        } while(!u.login.equals(login) && !u.nickname.equals(nickname));

        return false;*/
    }

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }
}
