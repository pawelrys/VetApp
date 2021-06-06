package jwzp.wp.VetApp.models.records;

public class UserRecord {
    private String username;
    private String hashedPassword;

    public UserRecord() {}

    public UserRecord(String username, String password) {
        this.setUsername(username);
        this.setHashedPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
