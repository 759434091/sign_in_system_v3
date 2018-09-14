package team.a9043.sign_in_system_version_2.pojo.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.User;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StuTimetable {
    private String usrId;
    private User student;
    private List<Course> courseList;

    public User getStudent() {
        return student;
    }

    public StuTimetable setStudent(User student) {
        this.student = student;
        return this;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public StuTimetable setCourseList(List<Course> courseList) {
        this.courseList = courseList;
        return this;
    }

    public String getUsrId() {
        return usrId;
    }

    public StuTimetable setUsrId(String usrId) {
        this.usrId = usrId;
        return this;
    }
}
