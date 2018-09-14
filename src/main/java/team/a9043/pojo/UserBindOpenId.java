package team.a9043.pojo;

public class UserBindOpenId {
    private User user;
    private String code;

    public User getUser() {
        return user;
    }

    public UserBindOpenId setUser(User user) {
        this.user = user;
        return this;
    }

    public String getCode() {
        return code;
    }

    public UserBindOpenId setCode(String code) {
        this.code = code;
        return this;
    }
}
