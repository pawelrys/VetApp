package jwzp.wp.VetApp.models.dtos;

public class UserData {
    private String username;
    private String password;

    public UserData() {}

    public UserData(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
