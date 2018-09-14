package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.*;

import java.util.List;

@Repository
public interface FileMapper {
    User checkTchExist(@Param("userId") String userId);

    Course checkCozExist(@Param("cozId") String cozId);

    User checkStudent(@Param("userId") String userId);

    List<Schedule> checkSchExist(@Param("cozId") String cozId);

    Location findLocation(@Param("locName") String locName);

    int addStudent(User user);

    int addTeacher(User user);

    int addCourse(AdmCourse course);

    int insertSchedule(Schedule schedule);

    int insertAttend(Attend attend);
}
