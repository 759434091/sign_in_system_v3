package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.*;

import java.util.List;

@Repository
public interface AdminMapper {
    List<User> findSuv();

    List<SuvSch> findSuvSch(@Param("userId") String userId);

    int getAllCourseNumber(@Param("cozName") String cozName, @Param("cozDepart") String cozDepart, @Param("cozGrade") Integer cozGrade);

    List<AdmCourse> showAllCourses(@Param("pageStep") int pageStep, @Param("pageSize") int pageSize, @Param("sortStr") String sortStr, @Param("isAsc") boolean isAsc,
                                   @Param("cozName") String cozName, @Param("cozDepart") String cozDepart, @Param("cozGrade") Integer cozGrade);

    List<Schedule> showAllCourses_getSch(@Param("cozIdList") List<String> cozIdList);

    List<User> showOneCozStuList(@Param("cozId") String cozId);

    OneSchSuvRec showOneSchRec(@Param("schId") int schId);

    /**
     * 查看督导历史记录
     *
     * @param userId 用户id
     * @return 返回督导历史记录
     */
    List<HisSuvRecRes> findHisSuvRecRes(@Param("userId") String userId);

    List<HisSuvRecRes> findHisAllRecRes(@Param("cozId") String cozId);

    List<SuvSch> showSuvCourses(String userId);

    List<CozAttRate> getCozAttRate();

    List<CozNumber> getCozNumber();

    int insertCozAttRate(CozAttRate cozAttRate);

    int insertCozNumber(CozNumber cozNumber);

    List<SchAbsRec> fSchAbsRecByCoz(@Param("cozId") String cozId);

    boolean grantSuv(@Param("userId") String userId);

    boolean revokeSuv(@Param("userId") String userId);

    List<Schedule> findScheduleByTime(@Param("schTime") int schTime);

    SuvMan findSuvMan(@Param("schId") int schId, @Param("siWeek") int siWeek);

    List<SignInRes> getLeaves(@Param("cozId") String cozId);

    List<AbsStatistics> selectAbcStatisticsNeverSignIn(@Param("week") Integer week,
                                                       @Param("cozName") String cozName,
                                                       @Param("cozDepart") String cozDepart,
                                                       @Param("cozGrade") Integer cozGrade,
                                                       @Param("userId") String userId,
                                                       @Param("userName") String userName);

    List<AbsStatistics> selectAbcStatistics(@Param("week") Integer week,
                                            @Param("cozName") String cozName,
                                            @Param("cozDepart") String cozDepart,
                                            @Param("cozGrade") Integer cozGrade,
                                            @Param("userId") String userId,
                                            @Param("userName") String userName);
}
