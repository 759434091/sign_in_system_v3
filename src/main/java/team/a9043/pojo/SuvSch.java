package team.a9043.pojo;

/**
 * 带周数的排课类
 */
public class SuvSch {
    private int suvId;
    private User student;
    private Schedule schedule;
    private Course course;
    private int suvWeek;
    private boolean suvLeave;
    private SuvMan suvMan;

    public User getStudent() {
        return student;
    }

    public SuvSch setStudent(User student) {
        this.student = student;
        return this;
    }

    public int getSuvId() {
        return suvId;
    }

    public SuvSch setSuvId(int suvId) {
        this.suvId = suvId;
        return this;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public SuvSch setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public int getSuvWeek() {
        return suvWeek;
    }

    public SuvSch setSuvWeek(int suvWeek) {
        this.suvWeek = suvWeek;
        return this;
    }

    public boolean isSuvLeave() {
        return suvLeave;
    }

    public SuvSch setSuvLeave(boolean suvLeave) {
        this.suvLeave = suvLeave;
        return this;
    }

    public Course getCourse() {
        return course;
    }

    public SuvSch setCourse(Course course) {
        this.course = course;
        return this;
    }

    public SuvMan getSuvMan() {
        return suvMan;
    }

    public SuvSch setSuvMan(SuvMan suvMan) {
        this.suvMan = suvMan;
        return this;
    }
}
