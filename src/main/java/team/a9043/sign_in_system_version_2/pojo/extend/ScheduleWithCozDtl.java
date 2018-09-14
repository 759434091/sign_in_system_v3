package team.a9043.sign_in_system_version_2.pojo.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.Schedule;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleWithCozDtl extends Schedule {
    private Course course;

    public Course getCourse() {
        return course;
    }

    public ScheduleWithCozDtl setCourse(Course course) {
        this.course = course;
        return this;
    }
}
