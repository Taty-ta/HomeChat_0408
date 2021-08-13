package server;

public interface AuthService {
    /**
     * Метод проверки наличия учетки
     * @param login не должен содержать пробелы
     * @param password не должен содержать пробелы
     * @return nickname если учетка существует, null если нет
     */
    String getNicknameByLoginAndPassword(String login, String password);

    /**
     * Метод для попытки регистрации новой учетной записи
     * @param true если рег прошла успешно
     * @param false если логин занят
     * @param var3
     * @return
     */
    boolean registration(String var1, String var2, String var3);

}
