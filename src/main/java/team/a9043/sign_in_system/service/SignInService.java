package team.a9043.sign_in_system.service;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.*;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.repository.SisCourseRepository;
import team.a9043.sign_in_system.repository.SisScheduleRepository;
import team.a9043.sign_in_system.repository.SisSignInDetailRepository;
import team.a9043.sign_in_system.repository.SisSignInRepository;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
public class SignInService {
    private String signInKeyFormat = "sis_ssId_%d_week_%d";
    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SisScheduleRepository sisScheduleRepository;
    @Resource
    private SisSignInRepository sisSignInRepository;
    @Resource
    private SisSignInDetailRepository sisSignInDetailRepository;
    @Resource
    private SisCourseRepository sisCourseRepository;

    @Transactional
    public boolean createSignIn(SisUser sisUser,
                                Integer ssId,
                                LocalDateTime localDateTime) throws InvalidTimeParameterException, InvalidPermissionException {
        SisSchedule sisSchedule = sisScheduleRepository
            .findById(ssId)
            .orElseThrow(() -> new InvalidParameterException("Invalid ssId: " + ssId));
        sisSchedule
            .getSisCourse()
            .getSisJoinCourseList()
            .parallelStream()
            .filter(sisJoinCourse ->
                sisJoinCourse.getJoinCourseType().equals(SisJoinCourse.JoinCourseType.TEACHING) &&
                    sisJoinCourse.getSisUser().getSuId().equals(sisUser.getSuId()))
            .findAny()
            .orElseThrow(() -> new InvalidPermissionException(
                "Invalid Permission in: " + ssId));


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
            .forEach(tSisUser -> redisTemplate.opsForHash()
                .put(key, tSisUser.getSuId(), false));

        scheduledThreadPoolExecutor.schedule(new EndSignInTask(key, ssId,
            week), 10L, TimeUnit.MINUTES);
        return true;
    }

    @Transactional
    public JSONObject getSignIn(Integer ssId, Integer week) {
        String key = String.format(signInKeyFormat, ssId, week);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("error", false);
            jsonObject.put("code", 1);
            jsonObject.put("message", "Processing");
            return jsonObject;
        }

        SisSchedule tSisSchedule = new SisSchedule();
        tSisSchedule.setSsId(ssId);
        SisSignIn tSisSignIn = new SisSignIn();
        tSisSignIn.setSisSchedule(tSisSchedule);
        tSisSignIn.setSsiWeek(week);
        Example<SisSignIn> sisSignInExample = Example.of(tSisSignIn);
        return sisSignInRepository
            .findOne(sisSignInExample)
            .map(sisSignIn -> {
                SisSchedule sisSchedule = sisSignIn.getSisSchedule();
                sisSchedule.setSisSignIns(null);
                sisSchedule.setSisSupervisions(null);

                SisCourse sisCourse = sisSchedule.getSisCourse();
                sisCourse.setSisSchedules(null);
                sisCourse.setMonitor(null);
                sisCourse.setSisJoinCourseList(null);

                Collection<SisSignInDetail> sisSignInDetails =
                    sisSignIn.getSisSignInDetails();
                sisSignInDetails.forEach(sisSignInDetail -> {
                    sisSignInDetail.setSisSignIn(null);
                    SisUser sisUser = sisSignInDetail.getSisUser();
                    sisUser.setSisSignInDetails(null);
                    sisUser.setSisJoinCourses(null);
                    sisUser.setSisCourses(null);
                    sisUser.setSisMonitorTrans(null);
                });

                entityManager.clear();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", true);
                jsonObject.put("error", false);
                jsonObject.put("code", 0);
                jsonObject.put("record", new JSONObject(sisSignIn));
                jsonObject.put("message", "End");
                return jsonObject;
            })
            .orElseGet(() -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", true);
                jsonObject.put("error", false);
                jsonObject.put("code", 2);
                jsonObject.put("message", "Not found");
                return jsonObject;
            });
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public JSONObject getSignIns(SisUser sisUser, String scId) throws IncorrectParameterException {
        SisCourse sisCourse = sisCourseRepository
            .findById(scId)
            .orElseThrow(() -> new IncorrectParameterException(
                "Incorrect scId: " + scId));

        sisCourse.setMonitor(null);
        sisCourse.setSisJoinCourseList(null);
        sisCourse
            .getSisSchedules()
            .forEach(sisSchedule -> {
                sisSchedule.setSisCourse(null);
                sisSchedule.setSisSupervisions(null);

                Collection<SisSignIn> sisSignIns = sisSchedule.getSisSignIns();
                sisSignIns
                    .forEach(sisSignIn -> {
                        sisSignIn.setSisSchedule(null);

                        Collection<SisSignInDetail> sisSignInDetails =
                            sisSignIn.getSisSignInDetails();
                        sisSignIn.setSisSignInDetails(sisSignInDetails
                            .parallelStream()
                            .filter(sisSignInDetail ->
                                sisSignInDetail.getSisUser().getSuId().equals(sisUser.getSuId()))
                            .peek(sisSignInDetail -> {
                                sisSignInDetail.setSisSignIn(null);
                                SisUser user = sisSignInDetail.getSisUser();
                                user.setSisMonitorTrans(null);
                                user.setSuPassword(null);
                                user.setSisCourses(null);
                                user.setSisJoinCourses(null);
                                user.setSisSignInDetails(null);
                            })
                            .collect(Collectors.toCollection(ArrayList::new)));
                    });
            });
        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("course", new JSONObject(sisCourse));
        return jsonObject;
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public JSONObject getSignIns(String scId) throws IncorrectParameterException {
        SisCourse sisCourse = sisCourseRepository
            .findById(scId)
            .orElseThrow(() -> new IncorrectParameterException(
                "Incorrect scId: " + scId));

        sisCourse.setMonitor(null);
        sisCourse.setSisJoinCourseList(null);
        sisCourse
            .getSisSchedules()
            .forEach(sisSchedule -> {
                sisSchedule.setSisCourse(null);
                sisSchedule.setSisSupervisions(null);

                Collection<SisSignIn> sisSignIns = sisSchedule.getSisSignIns();
                sisSignIns
                    .forEach(sisSignIn -> {
                        sisSignIn.setSisSchedule(null);

                        Collection<SisSignInDetail> sisSignInDetails =
                            sisSignIn.getSisSignInDetails();
                        sisSignInDetails
                            .parallelStream()
                            .forEach(sisSignInDetail -> {
                                sisSignInDetail.setSisSignIn(null);
                                SisUser sisUser = sisSignInDetail.getSisUser();
                                sisUser.setSisMonitorTrans(null);
                                sisUser.setSuPassword(null);
                                sisUser.setSisCourses(null);
                                sisUser.setSisJoinCourses(null);
                                sisUser.setSisSignInDetails(null);
                            });
                    });
            });
        entityManager.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("course", new JSONObject(sisCourse));
        return jsonObject;

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
            throw new IncorrectParameterException(
                String.format("Sign in not found: %d_%s", ssId, localDateTime));

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
