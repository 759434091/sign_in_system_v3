package team.a9043.sign_in_system.entity;

public class SisUser {
    private String suId;
    private String suName;
    private String suEnPassword;
    private String suOpenid;

    public String getSuId() {
        return suId;
    }

    public void setSuId(String suId) {
        this.suId = suId;
    }

    public String getSuName() {
        return suName;
    }

    public void setSuName(String suName) {
        this.suName = suName;
    }

    public String getSuEnPassword() {
        return suEnPassword;
    }

    public void setSuEnPassword(String suEnPassword) {
        this.suEnPassword = suEnPassword;
    }

    public String getSuOpenid() {
        return suOpenid;
    }

    public void setSuOpenid(String suOpenid) {
        this.suOpenid = suOpenid;
    }
}
