package team.a9043.sign_in_system_version_2.pojo;

import java.util.List;

public class Attendance {
    private String courseCozId;
    private List<User> userList;

    public String getCourseCozId() {
        return courseCozId;
    }

    public Attendance setCourseCozId(String courseCozId) {
        this.courseCozId = courseCozId;
        return this;
    }

    public List<User> getUserList() {
        return userList;
    }

    public Attendance setUserList(List<User> userList) {
        this.userList = userList;
        return this;
    }
}
