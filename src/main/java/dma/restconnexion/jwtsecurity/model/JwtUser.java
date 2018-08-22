package dma.restconnexion.jwtsecurity.model;

public class JwtUser {

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }
    public void setLogin(String Login) {
        this.login = Login;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}