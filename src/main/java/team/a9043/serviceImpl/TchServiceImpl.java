package team.a9043.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.mapper.TchMapper;
import team.a9043.pojo.*;
import team.a9043.service.TchService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TchServiceImpl implements TchService {

    private TchMapper tchMapper;

    @Autowired
    public TchServiceImpl(TchMapper tchMapper) {
        this.tchMapper = tchMapper;
    }

    @Override
    public List<User> getCozStudent(String cozId) {
        return tchMapper.getCozStudent(cozId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OneTchSchedule showTchCourses(User user) {
        return tchMapper.showTchCourses(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SuvRecord> fSuvRecByCoz(Schedule schedule) {
        return tchMapper.fSuvRecByCoz(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SchAbsRec> fSchAbsRecByCoz(Schedule schedule) {
        return tchMapper.fSchAbsRecByCoz(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean approveLeave(User user, SignInRes signInRes) {
        List<SignInRes> signInResList = tchMapper.getLeaves(user.getUserId());
        return isContain(signInRes, signInResList) && tchMapper.approveLeave(signInRes.getSiId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean rejectLeave(User user, SignInRes signInRes) {
        List<SignInRes> signInResList = tchMapper.getLeaves(user.getUserId());
        return isContain(signInRes, signInResList) && tchMapper.rejectLeave(signInRes.getSiId());
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SignInRes> getLeaves(User user) {
        return tchMapper.getLeaves(user.getUserId());
    }

    private boolean isContain(SignInRes signInRes, List<SignInRes> signInResList) {
        for (SignInRes tempSignInRes : signInResList) {
            if
                    ((tempSignInRes.getStudent().getUserId().equals(signInRes.getStudent().getUserId())) &&
                    (tempSignInRes.getSiWeek() == signInRes.getSiWeek()) &&
                    (tempSignInRes.getOneCozAndSch().getSchedule().getSchId() == signInRes.getOneCozAndSch().getSchedule().getSchId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean suspendClass(User user, Schedule schedule, int susWeek) {
        int schId = schedule.getSchId();
        User rightUser = tchMapper.findTchBySchId(schId);
        boolean res1 = rightUser.getUserId().equals(user.getUserId());
        boolean res2 = tchMapper.suspendClass_1(schId, susWeek);
        tchMapper.suspendClass_2(schId, susWeek);
        tchMapper.suspendClass_3(schId, susWeek);
        return res1 && res2;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Integer> findCozSuv(Schedule schedule) {
        return tchMapper.findCozSuv(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean setCozSuv(User user, SuvSch suvSch) {
        User rightUser = tchMapper.findTchBySchId(suvSch.getSchedule().getSchId());
        boolean res1 = rightUser.getUserId().equals(user.getUserId());
        tchMapper.removeCozSuv(suvSch);
        boolean res2 = tchMapper.setCozSuv(suvSch);
        return res1 && res2;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean removeCozSuv(User user, SuvSch suvSch) {
        User rightUser = tchMapper.findTchBySchId(suvSch.getSchedule().getSchId());
        boolean res1 = rightUser.getUserId().equals(user.getUserId());
        boolean res2 = tchMapper.removeCozSuv(suvSch);
        return res1 && res2;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean setCozSignIn(User user, SuvMan suvMan) {
        User rightUser = tchMapper.findTchBySchId(suvMan.getSchId());
        boolean res1 = rightUser.getUserId().equals(user.getUserId());
        tchMapper.removeCozSignIn(suvMan);
        boolean res2 = tchMapper.setCozSignIn(suvMan);
        return res1 && res2;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Integer> getCozSignIn(Schedule schedule) {
        return tchMapper.getCozSignIn(schedule.getSchId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean removeCozSignIn(SuvMan suvMan) {
        return tchMapper.removeCozSignIn(suvMan);
    }

    @Override
    public List<AbsStatistics> selectAbcStatistics(Schedule schedule, Integer week) {
        List<AbsStatistics> absStatisticsList1 = tchMapper.selectAbcStatistics(schedule.getSchId(),week);
        List<AbsStatistics> absStatisticsList2 = tchMapper.selectAbcStatisticsNeverSignIn(schedule.getSchId(),week);
        for (AbsStatistics absStatistics : absStatisticsList1) {
            int schId1 = absStatistics.getSchedule().getSchId();
            int week1 = absStatistics.getSarWeek();
            for (int j = 0; j < absStatisticsList2.size(); j++) {
                int schId2 = absStatisticsList2.get(j).getSchedule().getSchId();
                int week2 = absStatisticsList2.get(j).getSarWeek();
                if (schId1 == schId2 && week1 == week2) {
                    Set<User> userSet = new HashSet<>(absStatistics.getStudentList()); //list1转set
                    userSet.addAll(absStatisticsList2.get(j).getStudentList()); //list2并入
                    absStatistics.setStudentList(new ArrayList<>(userSet));//set 转 list
                    absStatisticsList2.remove(j); //不会出错,会跳出循环
                    break;
                }
            }
        }
        //list1&list2相同的排课已经并入, 且从list2 删除
        absStatisticsList1.addAll(absStatisticsList2);
        return absStatisticsList1;
    }
}
