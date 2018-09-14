package team.a9043.pojo;

/**
 * 一节排课记录
 */
public class OneCozAndSch {
    private Schedule schedule;
    private Course course;

    public Schedule getSchedule() {
        return schedule;
    }

    public OneCozAndSch setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public Course getCourse() {
        return course;
    }

    public OneCozAndSch setCourse(Course course) {
        this.course = course;
        return this;
    }

}
