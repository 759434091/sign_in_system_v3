package team.a9043.pojo;

import java.util.List;

public class OneStuSchedule {
    private User student;
    private List<Course> courses;

    public OneStuSchedule(String userId, String userName) {
        student = new User();
        student.setUserId(userId);
        student.setUserName(userName);
    }

    public User getStudent() {
        return student;
    }

    public OneStuSchedule setStudent(User student) {
        this.student = student;
        return this;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public OneStuSchedule setCourses(List<Course> courses) {
        this.courses = courses;
        return this;
    }
}
