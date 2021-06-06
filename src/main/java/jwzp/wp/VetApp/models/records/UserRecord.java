package jwzp.wp.VetApp.models.records;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity(name="users")
public class UserRecord {
    @Id
    private final int id;
    private final String username;
    private final String hashedPassword;
    private final String salt;

    public UserRecord(String username, String hashedPassword, String salt) {
        this.id = -1;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public UserRecord() {
        this.id = -1;
        this.username = "";
        this.hashedPassword = "";
        this.salt = "";
    }

    public static UserRecord createUserRecord(String username, String hashedPassword, String salt){
        return new UserRecord(username, hashedPassword, salt);
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public int getId() {
        return id;
    }
}
