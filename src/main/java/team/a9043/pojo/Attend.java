package team.a9043.pojo;

public class Attend {
    private String cozId;
    private String userId;

    public Attend() {
    }

    public Attend(String cozId, String userId) {
        this.cozId = cozId;
        this.userId = userId;
    }

    public String getCozId() {
        return cozId;
    }

    public Attend setCozId(String cozId) {
        this.cozId = cozId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Attend setUserId(String userId) {
        this.userId = userId;
        return this;
    }
}
