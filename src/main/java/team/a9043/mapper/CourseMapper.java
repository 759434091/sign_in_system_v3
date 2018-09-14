package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.*;

import java.util.List;


@Repository
public interface CourseMapper {
    /**
     * 获得该用户所有课程
     *
     * @param userId 用户id
     * @return 该用户所有课程
     */
    OneStuSchedule showCourses(@Param("userId") String userId);


    /**
     * 获得正上课程
     *
     * @param schedule 由当天日期转换成的排课对象
     * @return 一节课程
     */
    OneCozAndSch fSchedule(@Param("userId") String userId, @Param("schedule") Schedule schedule);

    List<SignInRes> fOneCozSignIn(@Param("userId") String userId, @Param("schId") int schId);

    List<SuvMan> findSuvMan(@Param("schId") int schId);

}
