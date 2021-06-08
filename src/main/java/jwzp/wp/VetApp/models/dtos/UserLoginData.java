package jwzp.wp.VetApp.models.dtos;

public class UserLoginData {
    private final String username;
    private final String password;

    public UserLoginData() {
        this.username = null;
        this.password = null;
    }

    public UserLoginData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
