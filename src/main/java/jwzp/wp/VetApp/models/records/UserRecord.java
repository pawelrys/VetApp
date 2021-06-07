package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.values.Role;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity(name="users")
public class UserRecord {
    @Id
    private final int id;
    private final String username;
    private final String hashedPassword;
    private final String salt;
    private final Role role;
    private final Integer connectedRecordId;

    public UserRecord(String username, String hashedPassword, String salt, Role role, Integer connectedRecordId) {
        this.id = -1;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.role = role;
        this.connectedRecordId =  connectedRecordId;
    }

    public UserRecord() {
        this.id = -1;
        this.username = "";
        this.hashedPassword = "";
        this.salt = "";
        this.role = null;
        this.connectedRecordId = null;
    }

    public static UserRecord createUserRecord(
            String username,
            String hashedPassword,
            String salt,
            Role role,
            Integer connectedRecordId
    ){
        return new UserRecord(username, hashedPassword, salt, role, connectedRecordId);
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

    public Role getRole() {
        return role;
    }

    public Integer getConnectedRecordId() {
        return connectedRecordId;
    }
}
