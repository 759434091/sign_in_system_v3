package team.a9043.pojo;

public class SuvTrans {
    private int sutrId;
    private SuvSch suvSch;
    private String userId;

    public int getSutrId() {
        return sutrId;
    }

    public SuvTrans setSutrId(int sutrId) {
        this.sutrId = sutrId;
        return this;
    }

    public SuvSch getSuvSch() {
        return suvSch;
    }

    public SuvTrans setSuvSch(SuvSch suvSch) {
        this.suvSch = suvSch;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public SuvTrans setUserId(String userId) {
        this.userId = userId;
        return this;
    }
}
