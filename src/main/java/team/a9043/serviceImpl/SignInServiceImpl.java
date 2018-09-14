package team.a9043.serviceImpl;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.JavaUtil.LocationUtil;
import team.a9043.JavaUtil.TransSchedule;
import team.a9043.cache.CacheProcess;
import team.a9043.mapper.SignInMapper;
import team.a9043.pojo.*;
import team.a9043.service.SignInService;
import team.a9043.task.ScheduleTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Service
public class SignInServiceImpl implements SignInService {

    private SignInMapper signInMapper;
    private CacheProcess cacheProcess;
    @Autowired
    ScheduleTask scheduleTask;

    @Autowired
    public SignInServiceImpl(SignInMapper signInMapper, CacheProcess cacheProcess) {
        this.signInMapper = signInMapper;
        this.cacheProcess = cacheProcess;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean checkSignIn(User user, Schedule schedule, LocalDateTime localDateTime) {
        return isSignIn(user.getUserId(), schedule.getSchId(), schedule.getSchWeek());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String getSignInRes(User student, Schedule schedule, LocalDateTime localDateTime) {
        class SignInBack {
            private boolean success;
            private boolean time;
            private boolean location;
            private boolean needSignIn;
            private boolean notSignIn;

            private SignInBack(boolean success, boolean time, boolean location, boolean needSignIn, boolean notSignIn) {
                this.success = success;
                this.time = time;
                this.location = location;
                this.needSignIn = needSignIn;
                this.notSignIn = notSignIn;
            }

            public boolean isNotSignIn() {
                return notSignIn;
            }

            public SignInBack setNotSignIn(boolean notSignIn) {
                this.notSignIn = notSignIn;
                return this;
            }

            public boolean isNeedSignIn() {
                return needSignIn;
            }

            public SignInBack setNeedSignIn(boolean needSignIn) {
                this.needSignIn = needSignIn;
                return this;
            }

            public boolean isTime() {
                return time;
            }

            public SignInBack setTime(boolean time) {
                this.time = time;
                return this;
            }

            public boolean isLocation() {
                return location;
            }

            public SignInBack setLocation(boolean location) {
                this.location = location;
                return this;
            }

            public boolean isSuccess() {
                return success;
            }

            public SignInBack setSuccess(boolean success) {
                this.success = success;
                return this;
            }
        }
        //scheduleTask.updateSignIn_x();
        Schedule standardSch = signInMapper.findSchedule(schedule.getSchId());
        SuvMan suvMan = signInMapper.findSuvMan(schedule.getSchId(), TransSchedule.getWeek(localDateTime));
        boolean finalRes = false;
        if (suvMan == null) {
            return JSON.toJSONString(new SignInBack(false, false, false, false, false));
        } else {
            /*判断地点有效性*/
            Location stdLocation = schedule.getLocation();
            Location location = standardSch.getLocation();
            boolean locRes = judgeLocation(location.getLocLat(), location.getLocLon(), stdLocation.getLocLat(), stdLocation.getLocLon());
            /*判断默认签到时间*/
            boolean defaultTimeRes = isDefaultTimeRes(TransSchedule.getTime(schedule.getSchTime()), localDateTime.toLocalTime(), suvMan.isSuvManAutoOpen());
            /*判断人工发起签到时间*/
            boolean manuelTimeRes = isManualTimeRes(suvMan, localDateTime.toLocalTime());
            /*判断是否已经签到*/
            boolean signIn = isSignIn(student.getUserId(), schedule.getSchId(), TransSchedule.getWeek(localDateTime));
            if ((defaultTimeRes || manuelTimeRes) && !signIn && locRes) {
                finalRes = cacheProcess.insertSignIn(student.getUserId(), schedule.getSchId(), localDateTime, schedule.getSchWeek()) != null;
            }
            return JSON.toJSONString(new SignInBack(finalRes, defaultTimeRes || manuelTimeRes, locRes, true, !signIn));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean getLeaveRes(User student, MultipartFile voucher, Schedule schedule, LocalDateTime localDateTime) {
        boolean signIn = isSignIn(student.getUserId(), schedule.getSchId(), schedule.getSchWeek());
        try {
            return !signIn &&
                    signInMapper.insertLeave(student.getUserId(), schedule.getSchId(), localDateTime, schedule.getSchWeek(), voucher.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isSignIn(String userId, int schId, int siWeek) {
        return signInMapper.isSignIn(userId, schId, siWeek) > 0;
    }

    private boolean isManualTimeRes(SuvMan suvMan, LocalTime tempLocalTime) {
        LocalDateTime deadDateTime = LocalDateTime.of(1970, Month.JANUARY, 1, 8, 0, 1);
        return (!suvMan.getSiTime().isEqual(deadDateTime)) && judgeTime(suvMan.getSiTime().toLocalTime(), tempLocalTime);
    }

    private boolean isDefaultTimeRes(LocalTime stdLocalTime, LocalTime tempLocalTime, boolean suvManAutoOpen) {
        return suvManAutoOpen && judgeTime(stdLocalTime, tempLocalTime);
    }

    private boolean judgeLocation(double lat1, double lng1, double lat2, double lng2) {
        double distance = LocationUtil.getDistance(lat1, lng1, lat2, lng2);
        return (distance <= 80);
    }

    private boolean judgeTime(LocalTime stdLocalTime, LocalTime tempLocalTime) {
        long minutes = stdLocalTime.until(tempLocalTime, ChronoUnit.MINUTES);
        return minutes <= 10 && minutes >= 0;
    }

}
