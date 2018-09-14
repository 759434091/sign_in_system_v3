package team.a9043.service;

import team.a9043.pojo.*;

import java.util.List;


public interface TchService {

    OneTchSchedule showTchCourses(User user);

    List<User> getCozStudent(String cozId);

    List<SuvRecord> fSuvRecByCoz(Schedule schedule);

    List<SchAbsRec> fSchAbsRecByCoz(Schedule schedule);

    boolean rejectLeave(User user, SignInRes signInRes);

    List<SignInRes> getLeaves(User user);

    boolean approveLeave(User user, SignInRes signInRes);

    boolean suspendClass(User user, Schedule schedule, int susWeek);

    List<Integer> findCozSuv(Schedule schedule);

    boolean setCozSuv(User user, SuvSch suvSch);

    boolean removeCozSuv(User user, SuvSch suvSch);

    boolean setCozSignIn(User user, SuvMan suvMan);

    List<Integer> getCozSignIn(Schedule schedule);

    boolean removeCozSignIn(SuvMan suvMan);

    List<AbsStatistics> selectAbcStatistics(Schedule schedule, Integer week);
}
