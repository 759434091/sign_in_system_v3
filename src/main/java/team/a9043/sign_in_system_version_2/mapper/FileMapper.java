package team.a9043.sign_in_system_version_2.mapper;

import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.Schedule;
import team.a9043.sign_in_system_version_2.pojo.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FileMapper {
    List<String> checkTchExist(@Param("usrId") String usrId);

    List<String> checkNewTeacher(@Param("teacherSet") Set<User> teacherSet);

    List<String> checkNewLocation(@Param("locStrSet") Set<String> locStrSet);

    List<String> checkNewCourse(@Param("courseList") List<Course> courseList);

    List<String> checkNewDepartment(@Param("depStrSet") Set<String> depStrSet);

    List<Map> checkNewAttendance(@Param("usrIdSet") Set<String> usrIdSet);

    List<String> checkNewStudent(@Param("usrIdSet") Set<String> usrIdSet);

    Integer addTeacher(@Param("teacherList") Set<User> teacherList);

    Integer addLocation(@Param("locStrSet") Set<String> locStrSet);

    Integer addCourse(@Param("courseList") List<Course> courseList);

    Integer addDepartment(@Param("depStrSet") Set<String> depStrSet);

    Integer addCozDep(@Param("cozDepMapSet") Set<HashMap<String, String>> cozDepMapSet);

    Integer addCozTch(@Param("cozTeaMapSet") Set<HashMap<String, String>> cozTeaMapSet);

    Integer addSchedule(@Param("scheduleList") List<Schedule> scheduleList);

    Integer addAttendance(@Param("attList") List<Map> attList);

    Integer addStudent(@Param("studentList") List<Map> studentList);
}
