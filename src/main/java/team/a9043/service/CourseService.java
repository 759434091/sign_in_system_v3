package team.a9043.service;

import team.a9043.pojo.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务
 */
public interface CourseService {
    /**
     * 查看课表
     *
     * @param userId 用户id
     * @return 课表
     */
    OneStuSchedule showCourses(String userId);

    /**
     * 查看正上课程
     *
     * @param localDateTime 时间
     * @return 一节排课课程
     */
    OneCozAndSch checkCourse(User user,LocalDateTime localDateTime);

    List<SignInRes> fOneCozSignIn(User user, Schedule schedule);

    int[] fOneCozAbsent(User user, Schedule schedule,LocalDateTime localDateTime);
}
