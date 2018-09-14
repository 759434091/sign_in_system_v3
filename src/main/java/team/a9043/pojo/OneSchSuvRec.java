package team.a9043.pojo;

public class OneSchSuvRec {
    private Schedule schedule;
    private User suvStudent;
    private SuvRecord suvRecord;

    public Schedule getSchedule() {
        return schedule;
    }

    public OneSchSuvRec setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public User getSuvStudent() {
        return suvStudent;
    }

    public OneSchSuvRec setSuvStudent(User suvStudent) {
        this.suvStudent = suvStudent;
        return this;
    }

    public SuvRecord getSuvRecord() {
        return suvRecord;
    }

    public OneSchSuvRec setSuvRecord(SuvRecord suvRecord) {
        this.suvRecord = suvRecord;
        return this;
    }
}
