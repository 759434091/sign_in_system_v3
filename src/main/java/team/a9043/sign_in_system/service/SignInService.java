package team.a9043.sign_in_system.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.*;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.repository.SisScheduleRepository;
import team.a9043.sign_in_system.repository.SisSignInDetailRepository;
import team.a9043.sign_in_system.repository.SisSignInRepository;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
public class SignInService {
    private String signInKeyFormat = "sis_ssId_%d_week_%d";
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SisScheduleRepository sisScheduleRepository;
    @Resource
    private SisSignInRepository sisSignInRepository;
    @Resource
    private SisSignInDetailRepository sisSignInDetailRepository;
    @Resource
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Transactional
    public boolean createSignIn(Integer ssId, LocalDateTime localDateTime) throws InvalidTimeParameterException {
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

        Integer week = JudgeTimeUtil.getWeek(localDateTime.toLocalDate());
        String key =
            String.format(signInKeyFormat, ssId,
                week);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return false;
        }
        redisTemplate.opsForHash()
            .put(key, "create_time", localDateTime);
        sisUserList
            .forEach(sisUser -> redisTemplate.opsForHash()
                .put(key, sisUser.getSuId(), false));

        scheduledThreadPoolExecutor.schedule(new EndSignInTask(key, ssId,
            week), 10L, TimeUnit.MINUTES);
        return true;
    }

    public boolean signIn(SisUser sisUser,
                          Integer ssId,
                          LocalDateTime localDateTime) throws InvalidTimeParameterException, IncorrectParameterException {
        String key =
            String.format(signInKeyFormat, ssId,
                JudgeTimeUtil.getWeek(localDateTime.toLocalDate()));

        LocalDateTime createTime = (LocalDateTime) redisTemplate
            .opsForHash()
            .get(key, "create_time");
        if (null == createTime)
            throw new IncorrectParameterException("Sign in not found: " +
                ssId + "_" + localDateTime);

        long until = createTime.until(localDateTime, ChronoUnit.MINUTES);
        if (until > 10 || until < 0) {
            return false;
        }

        redisTemplate.opsForHash().put(key, sisUser.getSuId(), true);
        return true;
    }

    @Getter
    @Setter
    class EndSignInTask implements Runnable {
        private String key;
        private Integer ssId;
        private Integer week;

        EndSignInTask(String key, Integer ssId, Integer week) {
            this.key = key;
            this.ssId = ssId;
            this.week = week;
        }

        @Override
        @Transactional
        public void run() {
            SisSchedule sisSchedule = new SisSchedule();
            sisSchedule.setSsId(ssId);
            SisSignIn sisSignIn = new SisSignIn();
            sisSignIn.setSisSchedule(sisSchedule);
            sisSignIn.setSsiWeek(week);

            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            Collection<SisSignInDetail> sisSignInDetails = map
                .entrySet()
                .stream()
                .map(entry -> {
                    SisUser sisUser = new SisUser();
                    sisUser.setSuId((String) entry.getKey());
                    SisSignInDetail sisSignInDetail = new SisSignInDetail();
                    sisSignInDetail.setSisUser(sisUser);
                    sisSignInDetail.setSsidStatus((boolean) entry.getValue());
                    sisSignInDetail.setSisSignIn(sisSignIn);
                    return sisSignInDetail;
                })
                .collect(Collectors.toList());

            sisSignIn.setSisSignInDetails(sisSignInDetails);
            sisSignInRepository.save(sisSignIn);
            sisSignInDetailRepository.saveAll(sisSignInDetails);
            redisTemplate.opsForHash().delete(key);
        }
    }
}
