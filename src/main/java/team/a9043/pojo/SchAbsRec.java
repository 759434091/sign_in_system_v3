package team.a9043.pojo;

import java.util.List;

public class SchAbsRec {
    private List<User> studentList;
    private Schedule schedule;
    private int sarWeek;

    public List<User> getStudentList() {
        return studentList;
    }

    public SchAbsRec setStudentList(List<User> studentList) {
        this.studentList = studentList;
        return this;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public SchAbsRec setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public int getSarWeek() {
        return sarWeek;
    }

    public SchAbsRec setSarWeek(int sarWeek) {
        this.sarWeek = sarWeek;
        return this;
    }
}
