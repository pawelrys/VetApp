package jwzp.wp.VetApp.models.dtos;

import jwzp.wp.VetApp.models.values.Role;

public class UserRegisterData {
    private final String username;
    private final String password;
    private final Role role;
    private final Integer connectedRecordId;

    public UserRegisterData() {
        this.role = null;
        this.connectedRecordId = null;
        this.username = null;
        this.password = null;
    }

    public UserRegisterData(String username, String password, Role role, Integer connectedRecordId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.connectedRecordId = connectedRecordId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Integer getConnectedRecordId() {
        return connectedRecordId;
    }
}
