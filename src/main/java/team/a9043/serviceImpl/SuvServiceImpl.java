package team.a9043.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.JavaUtil.TransSchedule;
import team.a9043.mapper.SuvMapper;
import team.a9043.pojo.*;
import team.a9043.service.SuvService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
public class SuvServiceImpl implements SuvService {


    private SuvMapper suvMapper;

    @Autowired
    public SuvServiceImpl(SuvMapper suvMapper) {
        this.suvMapper = suvMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SuvSch> showSuvCourses(String userId) {
        List<SuvSch> suvSchList = suvMapper.showSuvCourses(userId);
        for (SuvSch suvSch : suvSchList) {
            if (suvSch.getSuvMan().getSuvManId() == 0) {
                suvSch.setSuvMan(null);
            }
            suvSch.getCourse().setSchedules(null);
        }
        return suvSchList;
    }

    @Override
    public List<SuvSch> findToBeSupervised() {
        return suvMapper.findToBeSupervised();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public SuvSch findOneSuvCoz(String userId, LocalDateTime localDateTime) {
        Schedule tSchedule = TransSchedule.getSchedule(localDateTime);
        List<SuvSch> suvSchList = suvMapper.findOneSuvCozList(userId, tSchedule.getSchWeek());
        return findOneOSSS(tSchedule, suvSchList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean insertSuvRec(SuvRecord suvRecord) {
        return !isSuvRec(suvRecord.getSuvId()) && suvMapper.insertSuvRec(suvRecord);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean isSuvRec(int suvId) {
        return suvMapper.isSuvRec(suvId) > 0;
    }

    @Override
    public boolean isContainOSSS(SuvSch suvSch, List<SuvSch> suvSchList) {
        for (SuvSch tempSuvSch : suvSchList) {
            if (tempSuvSch.getSuvId() == suvSch.getSuvId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SignInRes> getLeaves(String userId) {
        List<SignInRes> signInResList = suvMapper.getLeaves(userId);
        for (SignInRes signInRes : signInResList) {
            signInRes.getOneCozAndSch().getCourse().setSchedules(null);
        }
        return signInResList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean approveLeave(User user, SignInRes signInRes) {
        List<SignInRes> signInResList = getLeaves(user.getUserId());
        return isContain(signInRes, signInResList) && suvMapper.approveLeave(signInRes.getSiId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean rejectLeave(User user, SignInRes signInRes) {
        List<SignInRes> signInResList = getLeaves(user.getUserId());
        return isContain(signInRes, signInResList) && suvMapper.rejectLeave(signInRes.getSiId());
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

    private SuvSch findOneOSSS(Schedule schedule, List<SuvSch> suvSchList) {
        for (SuvSch tSuvSch : suvSchList) {
            Schedule tSchedule = tSuvSch.getSchedule();
            if
                    ((schedule.getSchYear() == tSchedule.getSchYear()) &&
                    (schedule.getSchWeek() <= tSchedule.getSchWeek()) &&
                    (schedule.getSchDay() == tSchedule.getSchDay()) &&
                    (schedule.getSchTime() == tSchedule.getSchTime()) &&
                    ((schedule.getSchFortnight() == tSchedule.getSchFortnight()) || (tSchedule.getSchFortnight() == 0))) {
                return tSuvSch;
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<HisSuvRecRes> findHisSuvRecRes(User user) {
        return suvMapper.findHisSuvRecRes(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean suvLeave(User user, SuvSch suvSch) {
        User tUser = suvSch.getStudent();
        return user.getUserId().equals(tUser.getUserId()) && suvMapper.suvLeave(suvSch.getSchedule().getSchId(), suvSch.getSuvWeek(), user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean giveUpPower(User user, SuvSch suvSch) {
        User tUser = suvSch.getStudent();
        return user.getUserId().equals(tUser.getUserId()) && suvMapper.giveUpPower(suvSch);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public SuvMan getSignIn(SuvMan suvMan) {
        return suvMapper.getSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean initManSignIn(SuvMan suvMan) {
        return suvMapper.getSignIn(suvMan) == null && suvMapper.initManSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean initAutoSignIn(SuvMan suvMan) {
        return suvMapper.getSignIn(suvMan) == null && suvMapper.initAutoSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean closeAutoSignIn(SuvMan suvMan) {
        SuvMan suvManRes = suvMapper.getSignIn(suvMan);
        LocalDateTime deadTime = LocalDateTime.of(1970, Month.JANUARY, 1, 8, 0, 1);
        if (suvManRes != null) {
            boolean res1 = suvMapper.closeAutoSignIn(suvMan);
            if (res1 && suvManRes.getSiTime().isEqual(deadTime)) {
                return suvMapper.deleteSignIn(suvMan);
            } else {
                return res1;
            }
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean openAutoSignIn(SuvMan suvMan) {
        SuvMan suvManRes = suvMapper.getSignIn(suvMan);
        return suvManRes != null && suvMapper.openAutoSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean closeManSignIn(SuvMan suvMan) {
        SuvMan suvManRes = suvMapper.getSignIn(suvMan);
        if (suvManRes != null) {
            boolean res1 = suvMapper.closeManSignIn(suvMan);
            if (res1 && !suvManRes.isSuvManAutoOpen()) {
                return suvMapper.deleteSignIn(suvMan);
            } else {
                return res1;
            }
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean openManSignIn(SuvMan suvMan) {
        SuvMan suvManRes = suvMapper.getSignIn(suvMan);
        return suvManRes != null && suvMapper.openManSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean cancelSignIn(SuvMan suvMan) {
        return suvMapper.deleteSignIn(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String checkPower(SuvMan suvMan) {
        return suvMapper.checkPower(suvMan);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean receiveSuvSch(SuvSch suvSch) {
        return suvMapper.receiveSuvSch(suvSch);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean applyForTrans(SuvTrans suvTrans) {
        return suvMapper.applyForTrans(suvTrans);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean acceptSuvTrans(User user, SuvTrans suvTrans) {
        return suvMapper.acceptSuvTrans_1(suvTrans) && suvMapper.acceptSuvTrans_2(suvTrans.getSutrId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean refuseSuvTrans(SuvTrans suvTrans) {
        return suvMapper.refuseSuvTrans(suvTrans.getSutrId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<SuvTrans> getSuvTrans(User user) {
        return suvMapper.getSuvTrans(user.getUserId());
    }
}
