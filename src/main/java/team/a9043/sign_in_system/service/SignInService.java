package team.a9043.sign_in_system.service;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author a9043
 */
@Service
public class SignInService {
    private String signInKeyFormat = "sis_ssId_%d_week_%d";
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SisSignInMapper sisSignInMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisSignInDetailMapper sisSignInDetailMapper;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;

    @Transactional
    public boolean createSignIn(SisUser sisUser,
                                Integer ssId,
                                LocalDateTime localDateTime) throws InvalidTimeParameterException, InvalidPermissionException {
        SisSchedule sisSchedule = Optional
            .ofNullable(sisScheduleMapper.selectByPrimaryKey(ssId))
            .orElseThrow(() -> new InvalidParameterException("Invalid ssId: " + ssId));

        Integer week = JudgeTimeUtil.getWeek(localDateTime.toLocalDate());

        boolean isSuspend = sisSchedule.getSsSuspensionList()
            .stream()
            .anyMatch(tWeek -> tWeek.equals(week));
        if (isSuspend)
            throw new InvalidPermissionException(String.format(
                "Schedule %d week %d is in the suspension list",
                ssId, week));

        //get joinCourse
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andScIdEqualTo(sisSchedule.getScId());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        //check permission
        sisJoinCourseList.parallelStream()
            .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType()
                .equals(team.a9043.sign_in_system.entity.SisJoinCourse.JoinCourseType.TEACHING.ordinal()) &&
                sisJoinCourse.getSuId().equals(sisUser.getSuId()))
            .findAny()
            .orElseThrow(() -> new InvalidPermissionException(
                "Invalid Permission in: " + ssId));

        //get suIdList
        List<String> suIdList = sisJoinCourseList
            .parallelStream()
            .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType()
                .equals(team.a9043.sign_in_system.entity.SisJoinCourse.JoinCourseType.ATTENDANCE.ordinal()))
            .map(SisJoinCourse::getSuId)
            .collect(Collectors.toList());

        String key =
            String.format(signInKeyFormat, ssId,
                week);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return false;
        }
        redisTemplate.opsForHash()
            .put(key, "create_time", localDateTime);
        suIdList
            .forEach(suId -> redisTemplate.opsForHash()
                .put(key, suId, false));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        threadPoolTaskScheduler.schedule(new EndSignInTask(key, ssId,
            week), calendar.toInstant());
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

        SisSignInExample sisSignInExample = new SisSignInExample();
        sisSignInExample
            .createCriteria()
            .andSsIdEqualTo(ssId)
            .andSsiWeekEqualTo(week);

        return sisSignInMapper.selectByExample(sisSignInExample)
            .stream()
            .findAny()
            .map(sisSignIn -> {
                //join
                SisSchedule sisSchedule =
                    sisScheduleMapper.selectByPrimaryKey(ssId);

                if (null == sisSchedule) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", false);
                    jsonObject.put("error", true);
                    jsonObject.put("code", 3);
                    jsonObject.put("message", "schedule error: " + ssId);
                    return jsonObject;
                }
                SisCourse sisCourse =
                    sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());

                SisSignInDetailExample sisSignInDetailExample =
                    new SisSignInDetailExample();
                sisSignInDetailExample.createCriteria().andSsiIdEqualTo(sisSignIn.getSsiId());
                List<SisSignInDetail> sisSignInDetailList =
                    sisSignInDetailMapper.selectByExample(sisSignInDetailExample);

                List<String> suIdList =
                    sisSignInDetailList.parallelStream().map(SisSignInDetail::getSuId).collect(Collectors.toList());
                SisUserExample sisUserExample = new SisUserExample();
                sisUserExample.createCriteria().andSuIdIn(suIdList);
                List<SisUser> sisUserList;
                if (suIdList.isEmpty()) {
                    sisUserList = new ArrayList<>();
                } else {
                    sisUserList = sisUserMapper.selectByExample(sisUserExample);
                }

                //merge
                JSONObject sisScheduleJson = new JSONObject(sisSchedule);
                sisScheduleJson.put("sisCourse", new JSONObject(sisCourse));

                JSONArray sisSignInDetailJsonArray =
                    new JSONArray(sisSignInDetailList);
                CourseService.mergeWithSuIdJsonArrayEtSisUser(sisUserList,
                    sisSignInDetailJsonArray);

                JSONObject sisSignInJson = new JSONObject(sisSignIn);
                sisSignInJson.put("sisSignInDetailList",
                    sisSignInDetailJsonArray);
                sisSignInJson.put("sisSchedule", sisScheduleJson);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", true);
                jsonObject.put("error", false);
                jsonObject.put("code", 0);
                jsonObject.put("record", sisSignInJson);
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

    public JSONObject getSignIns(SisUser sisUser, String scId) throws IncorrectParameterException {
        //get course
        SisCourse sisCourse = Optional
            .ofNullable(sisCourseMapper.selectByPrimaryKey(scId))
            .orElseThrow(() -> new IncorrectParameterException(
                "Incorrect scId: " + scId));

        //join schedule
        SisScheduleExample sisScheduleExample = new SisScheduleExample();
        sisScheduleExample.createCriteria().andScIdEqualTo(scId);
        List<SisSchedule> sisScheduleList =
            sisScheduleMapper.selectByExample(sisScheduleExample);

        //join SignIn
        List<Integer> ssIdList = sisScheduleList.stream()
            .map(SisSchedule::getSsId)
            .collect(Collectors.toList());
        List<SisSignIn> sisSignInList;
        if (ssIdList.isEmpty()) {
            sisSignInList = new ArrayList<>();
        } else {
            SisSignInExample sisSignInExample = new SisSignInExample();
            sisSignInExample.createCriteria().andSsIdIn(ssIdList);
            sisSignInList = sisSignInMapper.selectByExample(sisSignInExample);
        }

        //join SignInDetail
        List<Integer> ssiIdList = sisSignInList.stream()
            .map(SisSignIn::getSsiId)
            .collect(Collectors.toList());
        List<SisSignInDetail> sisSignInDetailList;
        if (ssiIdList.isEmpty()) {
            sisSignInDetailList = new ArrayList<>();
        } else {
            SisSignInDetailExample sisSignInDetailExample =
                new SisSignInDetailExample();
            SisSignInDetailExample.Criteria criteria =
                sisSignInDetailExample.createCriteria();
            criteria.andSsiIdIn(ssiIdList);
            if (null != sisUser) {
                criteria.andSuIdEqualTo(sisUser.getSuId());
            }
            sisSignInDetailList =
                sisSignInDetailMapper.selectByExample(sisSignInDetailExample);
        }

        //join student
        List<String> suIdList = sisSignInDetailList.parallelStream()
            .map(SisSignInDetail::getSuId)
            .collect(Collectors.toList());
        List<SisUser> sisUserList =
            CourseService.getSisUserBySuIdList(suIdList, sisUserMapper);

        //merge student
        JSONArray sisSignInDetailJsonArray = new JSONArray(sisSignInDetailList);
        CourseService.mergeWithSuIdJsonArrayEtSisUser(sisUserList,
            sisSignInDetailJsonArray);

        //merge signInDetail
        JSONArray sisSignInJsonArray = new JSONArray(sisSignInList);
        sisSignInJsonArray.forEach(sisSignInObj -> {
            JSONObject sisSignInJson = (JSONObject) sisSignInObj;

            Integer ssiId = sisSignInJson.getInt("ssiId");

            List<JSONObject> sisSignInDetailJsonList = StreamSupport
                .stream(sisSignInDetailJsonArray.spliterator(),
                    false)
                .map(sisSignInDetailObj -> (JSONObject) sisSignInDetailObj)
                .filter(sisSignInDetailJson -> ssiId.equals(sisSignInDetailJson.getInt("ssiId")))
                .collect(Collectors.toList());
            sisSignInJson.put("sisSignInDetailList", sisSignInDetailJsonList);
        });

        //merge schedule
        JSONArray sisScheduleJsonArray = new JSONArray(sisScheduleList);
        sisScheduleJsonArray.forEach(sisScheduleObj -> {
            JSONObject sisScheduleJson = (JSONObject) sisScheduleObj;

            Integer ssId = sisScheduleJson.getInt("ssId");
            List<JSONObject> sisSignInJsonList = StreamSupport
                .stream(sisSignInJsonArray.spliterator(), false)
                .map(sisSignInObj -> (JSONObject) sisSignInObj)
                .filter(sisSignInJson -> ssId.equals(sisSignInJson.getInt(
                    "ssId")))
                .collect(Collectors.toList());

            sisScheduleJson.put("sisSignInList", sisSignInJsonList);
        });

        //merge course
        JSONObject sisCourseJson = new JSONObject(sisCourse);
        sisCourseJson.put("sisScheduleList", sisScheduleJsonArray);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("course", sisCourseJson);
        return jsonObject;
    }

    public JSONObject getSignIns(String scId) throws IncorrectParameterException {
        return getSignIns(null, scId);
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
        private Logger logger = LoggerFactory.getLogger(EndSignInTask.class);
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
            Map<Object, Object> map =
                redisTemplate.opsForHash().entries(key);
            SisSignIn sisSignIn = new SisSignIn();
            sisSignIn.setSsId(ssId);
            sisSignIn.setSsiWeek(week);
            sisSignIn.setSsiCreateTime((LocalDateTime) map.get("create_time"));
            boolean resSisSignIn = sisSignInMapper.insert(sisSignIn) > 0;
            if (!resSisSignIn || null == sisSignIn.getSsiId()) {
                logger.error(String.format(
                    "error insert sisSignIn: [%s, %s]", ssId, week));
                return;
            }

            map.remove("create_time");
            List<SisSignInDetail> sisSignInDetailList = map
                .entrySet()
                .parallelStream()
                .map(entry -> {
                    SisSignInDetail sisSignInDetail = new SisSignInDetail();
                    sisSignInDetail.setSuId((String) entry.getKey());
                    sisSignInDetail.setSsidStatus((boolean) entry.getValue());
                    sisSignInDetail.setSsiId(sisSignIn.getSsiId());
                    return sisSignInDetail;
                })
                .collect(Collectors.toList());

            if (sisSignInDetailList.isEmpty()) {
                logger.error("error sisSignInDetailList: empty");
                return;
            }

            boolean res =
                sisSignInDetailMapper.insertList(sisSignInDetailList) > 0;
            if (!res) {
                logger.error("error insert sisSignInDetailList: ");
                return;
            }
            redisTemplate.opsForHash().delete(key);
        }
    }
}
