package team.a9043.pojo;

import java.util.List;

public class OneTchSchedule {
    private User teacher;
    private List<AdmCourse> courses;

    public OneTchSchedule(String userId, String userName) {
        teacher = new User();
        teacher.setUserId(userId);
        teacher.setUserName(userName);
    }

    public User getTeacher() {
        return teacher;
    }

    public OneTchSchedule setTeacher(User teacher) {
        this.teacher = teacher;
        return this;
    }

    public List<AdmCourse> getCourses() {
        return courses;
    }

    public OneTchSchedule setCourses(List<AdmCourse> courses) {
        this.courses = courses;
        return this;
    }
}
