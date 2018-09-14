package team.a9043.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import team.a9043.pojo.*;

import java.util.List;

@Repository
public interface TchMapper {

    List<User> getCozStudent(@Param("cozId") String cozId);

    OneTchSchedule showTchCourses(@Param("userId") String userId);

    List<SuvRecord> fSuvRecByCoz(@Param("schId") int schId);

    List<SchAbsRec> fSchAbsRecByCoz(@Param("schId") int schId);

    List<SignInRes> getLeaves(@Param("userId") String userId);

    List<Integer> findCozSuv(@Param("schId") int schId);

    List<Integer> getCozSignIn(@Param("schId") int schId);

    boolean approveLeave(@Param("siId") int siId);

    boolean rejectLeave(@Param("siId") int siId);

    User findTchBySchId(@Param("schId") int schId);

    boolean suspendClass_1(@Param("schId") int schId, @Param("susWeek") int susWeek);

    boolean suspendClass_2(@Param("schId") int schId, @Param("susWeek") int susWeek);

    boolean suspendClass_3(@Param("schId") int schId, @Param("susWeek") int susWeek);

    boolean setCozSuv(SuvSch suvSch);

    boolean removeCozSuv(SuvSch suvSch);

    boolean setCozSignIn(SuvMan suvMan);

    boolean removeCozSignIn(SuvMan suvMan);

    List<AbsStatistics> selectAbcStatisticsNeverSignIn(@Param("schId") Integer schId, @Param("week") Integer week);

    List<AbsStatistics> selectAbcStatistics(@Param("schId") Integer schId, @Param("week") Integer week);
}
