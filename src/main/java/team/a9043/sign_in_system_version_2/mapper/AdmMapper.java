package team.a9043.sign_in_system_version_2.mapper;

import org.apache.ibatis.annotations.Param;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AdmMapper {
    List<User> getSupervisor();

    Integer modifySupervisor(
            @Param("set") Boolean set,
            @Param("usrId") String usrId);

    Integer getCourseCount(
            @Param("cozName") String cozName,
            @Param("grade") Integer grade,
            @Param("depName") String depName);

    List<Course> getCourse(
            @Param("cozName") String cozName,
            @Param("pageStep") Integer pageStep,
            @Param("pageSize") Integer pageSize,
            @Param("sortStr") String sortStr,
            @Param("isAsc") Boolean isAsc,
            @Param("grade") Integer grade,
            @Param("depName") String depName);

    List<User> getCozStudent(
            @Param("cozId") String cozId);

    List<SuvRecWithSuv> getCozSuvRec(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("cozId") String cozId);

    Integer insertSupervision(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("supervisionList") List<Supervision> supervisionList);

    Integer deleteSupervision(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("suvId") Integer suvId);

    List<Integer> getCourseSupervision(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("cozId") String cozId,
            @Param("week") Integer week);

    List<String> searchDepartment(@Param("depStr") String depStr);

    List<SignIn> getSignInRaw(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("siWeek") Integer siWeek);

    List<ScheduleWithCozDtl> getScheduleWithCozDtl(@Param("schIdSet") Set<Integer> schIdSet);

    List<Supervision> selectSupervision(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("supervisionList") List<Supervision> supervisionList);

    List<SignInRec> getSignInRecRaw(
            @Param("curYear") Integer curYear,
            @Param("curTerm") Boolean curTerm,
            @Param("siIdSet") Set<Integer> siIdSet);

    List<Attendance> getSignInAttendance(@Param("cozIdSet") Set<String> cozIdSet);

    Map getStaAttRate();

    List<Map> getAbsRankList(@Param("limitNum") Integer limitNum);
}
