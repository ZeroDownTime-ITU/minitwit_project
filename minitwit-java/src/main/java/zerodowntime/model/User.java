package zerodowntime.model;

public class User {
    private Integer userId;
    private String username;
    private String email;
    private String pwHash;

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPwHash() {
        return pwHash;
    }
}
