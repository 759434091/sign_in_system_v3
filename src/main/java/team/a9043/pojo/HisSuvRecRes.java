package team.a9043.pojo;

public class HisSuvRecRes {
    private int suvId;
    private User student;
    private OneCozAndSch oneCozAndSch;
    private SuvRecord suvRecord;
    private int suvWeek;

    public int getSuvId() {
        return suvId;
    }

    public HisSuvRecRes setSuvId(int suvId) {
        this.suvId = suvId;
        return this;
    }

    public User getStudent() {
        return student;
    }

    public HisSuvRecRes setStudent(User student) {
        this.student = student;
        return this;
    }

    public OneCozAndSch getOneCozAndSch() {
        return oneCozAndSch;
    }

    public HisSuvRecRes setOneCozAndSch(OneCozAndSch oneCozAndSch) {
        this.oneCozAndSch = oneCozAndSch;
        return this;
    }

    public SuvRecord getSuvRecord() {
        return suvRecord;
    }

    public HisSuvRecRes setSuvRecord(SuvRecord suvRecord) {
        this.suvRecord = suvRecord;
        return this;
    }

    public int getSuvWeek() {
        return suvWeek;
    }

    public HisSuvRecRes setSuvWeek(int suvWeek) {
        this.suvWeek = suvWeek;
        return this;
    }
}
