package team.a9043.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.pojo.*;

import java.util.List;

public interface AdminService {
    List<User> findSuv();

    List<SuvSch> findSuvSch(User user);

    OneStuSchedule showCourses(User user);

    int getAllCourseNumber(String cozName, String cozDepart, Integer cozGrade);

    List<AdmCourse> showAllCourses(String cozName, String cozDepart, Integer cozGrade, int page, int pageSize, String sortStr, boolean isAsc);

    List<User> getCozStudent(String cozId);

    List<User> showOneCozStuList(Course course);

    OneSchSuvRec showOneSchRec(Schedule schedule);

    /**
     * 获得所有督导课程
     *
     * @param userId 用户id
     * @return 督导课程列表
     */
    List<SuvSch> showSuvCourses(String userId);

    /**
     * 查看督导历史记录
     *
     * @param userId 用户
     * @return 返回督导历史记录
     */
    List<HisSuvRecRes> findHisSuvRecRes(String userId);

    List<HisSuvRecRes> findHisAllRecRes(String userId);

    int updateCozAttRate();

    int updateCozNumber();

    List<SchAbsRec> fSchAbsRecByCoz(String cozId);

    boolean grantSuv(User user);

    boolean revokeSuv(User user);

    List<Schedule> findScheduleByTime(int schTime);

    SuvMan findSuvMan(int schId, int siWeek);

    List<Integer> findCozSuv(Schedule schedule);

    boolean setCozSuv(SuvSch suvSch);

    boolean removeCozSuv(SuvSch suvSch);

    boolean setCozSignIn(SuvMan suvMan);

    List<Integer> getCozSignIn(Schedule schedule);

    boolean removeCozSignIn(SuvMan suvMan);

    List<SignInRes> getLeaves(String cozId);

    boolean approveLeave(SignInRes signInRes);

    boolean rejectLeave(SignInRes signInRes);

    JSONObject selectAbcStatistics(Integer week, Integer page, String cozName, String cozDepart, Integer cozGrade, String userId, String userName);
}
