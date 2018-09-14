package team.a9043.sign_in_system_version_2.mapper;

import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInRecOnLeaveWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SupervisionMapper {
    User getUserBySuvId(@Param("curYear") Integer curYear,
                        @Param("curTerm") Boolean curTerm,
                        @Param("suvId") Integer suvId);

    User getUserBySiId(@Param("curYear") Integer curYear,
                       @Param("curTerm") Boolean curTerm,
                       @Param("siId") Integer siId);

    User getUserBySchAndWeek(@Param("curYear") Integer curYear,
                             @Param("curTerm") Boolean curTerm,
                             @Param("suvWeek") Integer suvWeek,
                             @Param("scheduleSchId") Integer scheduleSchId);

    List<Supervision> getSuvCourseList(@Param("curYear") Integer curYear,
                                       @Param("curTerm") Boolean curTerm,
                                       @Param("curWeek") Integer curWeek,
                                       @Param("isIgnore") Boolean isIgnore,
                                       @Param("usrId") String usrId);

    List<SignInRecOnLeaveWithCozDtl> getStuLeaveRec(@Param("curYear") Integer curYear,
                                                    @Param("curTerm") Boolean curTerm,
                                                    @Param("usrId") String usrId);

    Integer receiveSuvSchedule (@Param("curYear") Integer curYear,
                                @Param("curTerm") Boolean curTerm,
                                @Param("usrId") String usrId,
                                @Param("schId") Integer schId,
                                @Param("suvWeekList") List<Integer> suvWeekList);

    @Deprecated
    SignInWithCozDtl getSignInWithCozDtl(@Param("curYear") Integer curYear,
                                         @Param("curTerm") Boolean curTerm,
                                         @Param("siWeek") Integer suvWeek,
                                         @Param("scheduleSchId") Integer scheduleSchId);

    SignIn getSignInBySchAndWeek(@Param("curYear") Integer curYear,
                                 @Param("curTerm") Boolean curTerm,
                                 @Param("siWeek") Integer siWeek,
                                 @Param("scheduleSchId") Integer scheduleSchId);

    List<SuvTrans> getSuvTransByUser(@Param("curYear") Integer curYear,
                                     @Param("curTerm") Boolean curTerm,
                                     @Param("usrId") String usrId);

    List<Map<String, Object>> getSuvRaw(@Param("curYear") Integer curYear,
                                  @Param("curTerm") Boolean curTerm,
                                  @Param("pageSize") Integer pageSize,
                                  @Param("pageStep") Integer pageStep);

    SuvRecWithSuv getSuvRec(@Param("curYear") Integer curYear,
                                  @Param("curTerm") Boolean curTerm,
                                  @Param("suvId") Integer suvId);

    Integer insertSuvRec(@Param("curYear") Integer curYear,
                     @Param("curTerm") Boolean curTerm,
                     @Param("suvRecNum") Integer suvRecNum,
                     @Param("suvRecBadNum") Integer suvRecBadNum,
                     @Param("suvRecName") String suvRecName,
                     @Param("suvRecInfo") String suvRecInfo,
                     @Param("supervisionSuvId") Integer supervisionSuvId);

    Integer insertSignIn(@Param("curYear") Integer curYear,
                     @Param("curTerm") Boolean curTerm,
                     @Param("siWeek") Integer siWeek,
                     @Param("siTime") LocalDateTime siTime,
                     @Param("siAuto") Boolean siAuto,
                     @Param("scheduleSchId") Integer scheduleSchId);

    Integer insertSuvTrans(@Param("curYear") Integer curYear,
                       @Param("curTerm") Boolean curTerm,
                       @Param("usrId") String usrId,
                       @Param("suvId") Integer suvId);

    Integer updateSignIn(@Param("curYear") Integer curYear,
                     @Param("curTerm") Boolean curTerm,
                     @Param("siWeek") Integer siWeek,
                     @Param("siTime") LocalDateTime siTime,
                     @Param("siAuto") Boolean siAuto,
                     @Param("scheduleSchId") Integer scheduleSchId);

    Integer updateSignInRec(@Param("curYear") Integer curYear,
                        @Param("curTerm") Boolean curTerm,
                        @Param("sirApprove") Integer siApprove,
                        @Param("sirId") Integer sirId);

    Integer changePower(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("usrId") String usrId,
            @Param("suvId") Integer suvId);

    Integer deleteSignInRec(@Param("curYear") Integer curYear,
                        @Param("curTerm") Boolean curTerm,
                        @Param("sirId") Integer sirId);

    Integer deleteSignIn(@Param("curYear") Integer curYear,
                     @Param("curTerm") Boolean curTerm,
                     @Param("siWeek") Integer siWeek,
                     @Param("scheduleSchId") Integer scheduleSchId);

    Integer deleteSuvTrans(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("usrId") String usrId,
            @Param("suvId") Integer suvId);
}