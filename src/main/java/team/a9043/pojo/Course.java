package team.a9043.pojo;

import java.util.List;

/**
 * 课程POJO
 */
public class Course {
    private String cozId;
    private String cozName;
    private User teacher;
    private List<Schedule> schedules;

    public Course() {
    }

    public Course(String cozId, String cozName, User teacher, List<Schedule> schedules) {
        this.cozId = cozId;
        this.cozName = cozName;
        this.teacher = teacher;
        this.schedules = schedules;
    }

    public String getCozId() {
        return cozId;
    }

    public Course setCozId(String cozId) {
        this.cozId = cozId;
        return this;
    }

    public String getCozName() {
        return cozName;
    }

    public Course setCozName(String cozName) {
        this.cozName = cozName;
        return this;
    }

    public User getTeacher() {
        return teacher;
    }

    public Course setTeacher(User teacher) {
        this.teacher = teacher;
        return this;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Course setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        return this;
    }
}
