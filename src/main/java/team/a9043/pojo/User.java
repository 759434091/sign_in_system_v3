package team.a9043.pojo;

import java.io.Serializable;

/**
 * 用户对象
 */
public class User implements Serializable {
    private static final long serialVersionUID = 3610705598508282247L;
    private String userId;
    private String userPwd;
    private String userPermit;
    private String userName;
    private String openId;

    public User() {
    }

    public User(String userId, String userPwd, String userPermit, String userName, String openId) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userPermit = userPermit;
        this.userName = userName;
        this.openId = openId;
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public User setUserPwd(String userPwd) {
        this.userPwd = userPwd;
        return this;
    }

    public String getUserPermit() {
        return userPermit;
    }

    public User setUserPermit(String userPermit) {
        this.userPermit = userPermit;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getOpenId() {
        return openId;
    }

    public User setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof User) {
            return ((User) obj).userId.equals(this.userId) && ((User) obj).userName.equals(this.userName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.userId + "_" + this.userName).hashCode();
    }
}
