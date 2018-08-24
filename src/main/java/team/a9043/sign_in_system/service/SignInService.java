package team.a9043.sign_in_system.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisJoinCourse;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.repository.SisScheduleRepository;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
public class SignInService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SisScheduleRepository sisScheduleRepository;
    @Resource
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Transactional
    public void createSignIn(Integer ssId, LocalDateTime localDateTime) throws InvalidTimeParameterException {
        SisSchedule sisSchedule = sisScheduleRepository
            .findById(ssId)
            .orElseThrow(() -> new InvalidParameterException("Invalid ssId: " + ssId));

        List<SisUser> sisUserList = sisSchedule
            .getSisCourse()
            .getSisJoinCourseList()
            .parallelStream()
            .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType().equals(SisJoinCourse.JoinCourseType.ATTENDANCE))
            .map(SisJoinCourse::getSisUser)
            .collect(Collectors.toList());

        String hashKey =
            String.format("sis_ssId_%d_week_%d", ssId,
                JudgeTimeUtil.getWeek(localDateTime.toLocalDate()));

        redisTemplate.opsForHash()
            .put(hashKey, "create_time", localDateTime);
        sisUserList
            .forEach(sisUser -> redisTemplate.opsForHash()
                .put(hashKey, sisUser.getSuId(), false));

        scheduledThreadPoolExecutor.schedule()
        Timer timer = new Timer();
        timer.schedule(new EndSignInTask(hashKey), 60 * 10 * 1000);
    }

    public boolean signIn(SisUser sisUser,
                          Integer ssId,
                          LocalDateTime localDateTime) throws InvalidTimeParameterException, IncorrectParameterException {
        String hashKey =
            String.format("sis_ssId_%d_week_%d", ssId,
                JudgeTimeUtil.getWeek(localDateTime.toLocalDate()));

        LocalDateTime createTime = (LocalDateTime) redisTemplate
            .opsForHash()
            .get(hashKey, "create_time");
        if (null == createTime)
            throw new IncorrectParameterException("Sign in not found: " +
                ssId + "_" + localDateTime);

        long until = createTime.until(localDateTime, ChronoUnit.MINUTES);
        if (until > 10 || until < 0) {
            return false;
        }

        redisTemplate.opsForHash().put(hashKey, sisUser.getSuId(), true);
        return true;
    }

    @Getter
    @Setter
    static class EndSignInTask extends TimerTask {
        private String hashKey;

        EndSignInTask(String hashKey) {
            this.hashKey = hashKey;
        }

        @Override
        public void run() {
            System.out.println("task: " + hashKey);
        }
    }
}
