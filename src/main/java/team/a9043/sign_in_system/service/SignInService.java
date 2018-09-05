package team.a9043.sign_in_system.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.aspect.SupervisionAspect;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author a9043
 */
@Slf4j
@Service
public class SignInService {
    private String signInKeyFormat = "sis_ssId_%s_week_%s";
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Resource(name = "sisRedisTemplate")
    private RedisTemplate<String, Object> sisRedisTemplate;
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
    @Resource
    private SisLocationMapper sisLocationMapper;
    @Resource
    private SupervisionAspect supervisionAspect;

    @Transactional
    public JSONObject createSignIn(SisUser sisUser,
                                   Integer ssId,
                                   LocalDateTime currentDateTime) throws InvalidTimeParameterException, InvalidPermissionException {
        SisSchedule sisSchedule = Optional
            .ofNullable(sisScheduleMapper.selectByPrimaryKey(ssId))
            .orElseThrow(() -> new InvalidParameterException("Invalid ssId: " + ssId));

        Integer week = JudgeTimeUtil.getWeek(currentDateTime.toLocalDate());

        //check week
        if (week < sisSchedule.getSsStartWeek() || week > sisSchedule.getSsEndWeek()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Invalid week: " + week);
            return jsonObject;
        }

        //check suspend
        boolean isSuspend = sisSchedule.getSsSuspensionList()
            .stream()
            .anyMatch(tWeek -> tWeek.equals(week));
        if (isSuspend) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", String.format(
                "Schedule %d week %d is in the suspension list",
                ssId, week));
            return jsonObject;
        }

        //check is end
        SisSignInExample sisSignInExample = new SisSignInExample();
        sisSignInExample.createCriteria().andSsIdEqualTo(ssId).andSsiWeekEqualTo(week);
        if (!sisSignInMapper.selectByExample(sisSignInExample).isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Check-in has been launched and " +
                "completed: week " + week);
            return jsonObject;
        }

        //get joinCourse
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andScIdEqualTo(sisSchedule.getScId());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        //check permission
        if (!sisUser.getSuAuthorities()
            .contains(new SimpleGrantedAuthority("ADMINISTRATOR")))
            sisJoinCourseList.parallelStream()
                .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType()
                    .equals(team.a9043.sign_in_system.entity.SisJoinCourse.JoinCourseType.TEACHING.ordinal()) &&
                    sisJoinCourse.getSuId().equals(sisUser.getSuId()))
                .findAny()
                .orElseThrow(() -> new InvalidPermissionException(
                    "Invalid Permission in: " + ssId));

        //get suIdList
        Map<String, Object> hashMap = sisJoinCourseList
            .parallelStream()
            .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType()
                .equals(team.a9043.sign_in_system.entity.SisJoinCourse.JoinCourseType.ATTENDANCE.ordinal()))
            .map(SisJoinCourse::getSuId)
            .collect(Collectors.toMap(Function.identity(),
                (s) -> Boolean.FALSE));
        if (hashMap.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "No student in this course");
            return jsonObject;
        }

        String key =
            String.format(signInKeyFormat, ssId,
                week);

        if (Boolean.TRUE.equals(sisRedisTemplate.hasKey(key))) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Sign in is processing");
            return jsonObject;
        }

        log.info("create signIn schedule: " + key);
        if (null != sisSchedule.getSlId()) {
            SisLocation sisLocation =
                sisLocationMapper.selectByPrimaryKey(sisSchedule.getSlId());
            if (null != sisLocation &&
                null != sisLocation.getSlLat() &&
                null != sisLocation.getSlLong()) {
                hashMap.put("loc_lat", sisLocation.getSlLat());
                hashMap.put("loc_long", sisLocation.getSlLong());
                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                        "sign in schedule has location: key %s -> %s",
                        key,
                        new JSONObject(sisLocation)));
                }
            }
        }


        hashMap.put("create_time", currentDateTime);
        sisRedisTemplate.opsForHash().putAll(key, hashMap);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        threadPoolTaskScheduler.schedule(new EndSignInTask(key, ssId,
            week), calendar.toInstant());

        log.info("start signIn schedule and end at: " + calendar.toInstant().toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("message", "success");
        return jsonObject;
    }

    public JSONObject getSignIn(Integer ssId, Integer week) {
        String key = String.format(signInKeyFormat, ssId, week);
        if (Boolean.TRUE.equals(sisRedisTemplate.hasKey(key))) {
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

            String keyPattern = String.format(signInKeyFormat, ssId, "*");
            List<JSONObject> processingList =
                Optional
                    .ofNullable(sisRedisTemplate.keys(keyPattern))
                    .map(c -> c.stream()
                        .map(key -> {
                            Matcher matcher = Pattern.compile(
                                "sis_ssId_\\d+_week_(\\d+)")
                                .matcher(key);
                            if (!matcher.find())
                                return null;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("ssId", ssId);
                            jsonObject.put("week",
                                Integer.valueOf(matcher.group(1)));
                            return jsonObject;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                    .orElse(null);

            List<JSONObject> sisSignInJsonList = StreamSupport
                .stream(sisSignInJsonArray.spliterator(), false)
                .map(sisSignInObj -> (JSONObject) sisSignInObj)
                .filter(sisSignInJson -> ssId.equals(sisSignInJson.getInt(
                    "ssId")))
                .collect(Collectors.toList());

            sisScheduleJson.put("sisProcessingList", processingList);
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

    @SuppressWarnings("ConstantConditions")
    public JSONObject signIn(SisUser sisUser,
                             Integer ssId,
                             LocalDateTime currentDateTime,
                             JSONObject locationJson) throws InvalidTimeParameterException, IncorrectParameterException, InvalidPermissionException {
        if (log.isDebugEnabled()) {
            log.debug("User " + sisUser.getSuId() + " try to signIn");
        }
        String key =
            String.format(signInKeyFormat, ssId,
                JudgeTimeUtil.getWeek(currentDateTime.toLocalDate()));

        if (!sisRedisTemplate.hasKey(key))
            throw new IncorrectParameterException(
                String.format("Sign in not found: %d_%s", ssId,
                    currentDateTime));

        LocalDateTime createTime = (LocalDateTime) sisRedisTemplate
            .opsForHash()
            .get(key, "create_time");
        if (null == createTime) {
            String errStr = String.format(
                "SignIn error: No create_time %d_%s", ssId, currentDateTime);
            log.error(errStr);
            throw new InvalidPermissionException(errStr);
        }

        long until = createTime.until(currentDateTime, ChronoUnit.MINUTES);
        if (until > 10 || until < 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Error time until: " + until);
            return jsonObject;
        }

        //todo judge
        if (sisRedisTemplate.opsForHash().hasKey(key, "loc_lat") &&
            sisRedisTemplate.opsForHash().hasKey(key, "loc_long")) {
            Double locLat = (Double) sisRedisTemplate.opsForHash().get(key,
                "loc_lat");
            Double locLong = (Double) sisRedisTemplate.opsForHash().get(key,
                "loc_long");
        }

        sisRedisTemplate.opsForHash().put(key, sisUser.getSuId(), true);
        if (log.isDebugEnabled()) {
            log.debug("User " + sisUser.getSuId() + " successfully signIn");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("message", "Success");
        return jsonObject;
    }

    @Getter
    @Setter
    public class EndSignInTask implements Runnable {
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
                sisRedisTemplate.opsForHash().entries(key);

            map.remove("create_time");
            map.remove("loc_lat");
            map.remove("loc_long");

            //create signInDetail list
            List<SisSignInDetail> sisSignInDetailList = map
                .entrySet()
                .parallelStream()
                .map(entry -> {
                    SisSignInDetail sisSignInDetail = new SisSignInDetail();
                    sisSignInDetail.setSuId((String) entry.getKey());
                    sisSignInDetail.setSsidStatus((boolean) entry.getValue());
                    return sisSignInDetail;
                })
                .collect(Collectors.toList());

            if (sisSignInDetailList.isEmpty()) {
                logger.error("error sisSignInDetailList: empty");
                return;
            }

            //create signIn
            SisSignIn sisSignIn = new SisSignIn();
            sisSignIn.setSsId(ssId);
            sisSignIn.setSsiWeek(week);
            sisSignIn.setSsiCreateTime((LocalDateTime) map.get("create_time"));
            long attNum = sisSignInDetailList.stream()
                .filter(sisSignInDetail -> Boolean.TRUE.equals(sisSignInDetail.getSsidStatus()))
                .count();
            long totalNum = sisSignInDetailList.size();
            double attRate = (double) attNum / totalNum;
            sisSignIn.setSsiAttRate(attRate);

            //insert signIn
            boolean resSisSignIn = sisSignInMapper.insert(sisSignIn) > 0;
            if (!resSisSignIn) {
                logger.error(String.format(
                    "error insert sisSignIn: [%s, %s]", ssId, week));
                return;
            }

            //insert signInDetail
            sisSignInDetailList.parallelStream().forEach(sisSignInDetail -> {
                sisSignInDetail.setSsiId(sisSignIn.getSsiId());
            });
            boolean res =
                sisSignInDetailMapper.insertList(sisSignInDetailList) > 0;
            if (!res) {
                logger.error("error insert sisSignInDetailList: " + new JSONArray(sisSignInDetailList).toString(2));
                return;
            }

            //finish
            sisRedisTemplate.delete(key);
            log.info("start update scAttRate: by schedule " + ssId);
            supervisionAspect.updateAttRate(ssId);
        }
    }
}
