package team.a9043.sign_in_system_version_2.mapper;

import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system_version_2.pojo.Course;

import java.util.List;

public interface TchMapper {
    List<Course> selectCourseByTch(@Param("usrId") String usrId);
}
