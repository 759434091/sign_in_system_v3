package team.a9043.sign_in_system_version_2.mapper;

import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system_version_2.pojo.Schedule;
import team.a9043.sign_in_system_version_2.pojo.SignIn;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithHistorySignIn;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.StuTimetable;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseMapper {
    StuTimetable getStuTimetable(@Param("usrId") String usrId);

    Schedule getScheduleByPrimaryKey(Schedule schedule);

    SignIn getSignInByWeekAndSch(@Param("curYear") Integer curYear,
                                 @Param("curTerm") Boolean curTerm,
                                 @Param("schId") Integer schId,
                                 @Param("siWeek") Integer siWeek);

    Boolean signInInDatabase(@Param("curYear") Integer curYear,
                             @Param("curTerm") Boolean curTerm,
                             @Param("sirTime") LocalDateTime sirTime,
                             @Param("siId") Integer siId,
                             @Param("usrId") String usrId);

    Boolean stuLeave(@Param("curYear") Integer curYear,
                     @Param("curTerm") Boolean curTerm,
                     @Param("usrId") String usrId,
                     @Param("siId") Integer siId,
                     @Param("sirVoucher") byte[] sirVoucher,
                     @Param("sirTime") LocalDateTime sirTime);

    /**
     * 获得签到历史 以及 老师记录缺勤
     * @param curYear required
     * @param curTerm required
     * @param cozId required
     * @param usrId optional
     * @return 签到历史
     */
    List<ScheduleWithHistorySignIn> getHistorySignIn(@Param("curYear") Integer curYear,
                                                     @Param("curTerm") Boolean curTerm,
                                                     @Param("cozId") String cozId,
                                                     @Param("usrId") String usrId);

    List<SignInWithCozDtl> getNeedSignInByUser(@Param("curYear") Integer curYear,
                                               @Param("curTerm") Boolean curTerm,
                                               @Param("usrId") String usrId);
}