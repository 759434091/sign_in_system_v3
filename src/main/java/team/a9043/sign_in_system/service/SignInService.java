package team.a9043.sign_in_system.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system.aspect.SupervisionAspect;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.service_pojo.OperationResponse;
import team.a9043.sign_in_system.service_pojo.SignInProcessing;
import team.a9043.sign_in_system.service_pojo.VoidOperationResponse;
import team.a9043.sign_in_system.service_pojo.VoidSuccessOperationResponse;
import team.a9043.sign_in_system.util.LocationUtil;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Slf4j
@Service
public class SignInService {
    private String signInKeyFormat = "sis_ssId_%s_week_%s";
    @Value("${location.limit}")
    private int MAX_DISTANCE;
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
    public VoidOperationResponse createSignIn(SisUser sisUser,
                                              Integer ssId,
                                              LocalDateTime currentDateTime) throws InvalidTimeParameterException, InvalidPermissionException {
        SisSchedule sisSchedule = Optional
            .ofNullable(sisScheduleMapper.selectByPrimaryKey(ssId))
            .orElseThrow(() -> new InvalidParameterException("Invalid ssId: " + ssId));

        Integer week = JudgeTimeUtil.getWeek(currentDateTime.toLocalDate());

        //check week
        if (week < sisSchedule.getSsStartWeek() || week > sisSchedule.getSsEndWeek())
            return new VoidOperationResponse(false, "Invalid week: " + week);

        //check suspend
        boolean isSuspend = sisSchedule.getSsSuspensionList()
            .stream()
            .anyMatch(tWeek -> tWeek.equals(week));
        if (isSuspend) return new VoidOperationResponse(false, String.format(
            "排课编号%s在第%s周已经停课", ssId, week));

        //check is end
        SisSignInExample sisSignInExample = new SisSignInExample();
        sisSignInExample.createCriteria().andSsIdEqualTo(ssId).andSsiWeekEqualTo(week);
        if (!sisSignInMapper.selectByExample(sisSignInExample).isEmpty())
            return new VoidOperationResponse(false,
                "Check-in has been launched and completed: week " + week);

        //get joinCourse
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andScIdEqualTo(sisSchedule.getScId());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        //check permission
        if (!sisUser.getSuAuthorities()
            .contains(new SimpleGrantedAuthority("ADMINISTRATOR")))
            sisJoinCourseList.stream()
                .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType()
                    .equals(SisJoinCourse.JoinCourseType.TEACHING.ordinal()) &&
                    sisJoinCourse.getSuId().equals(sisUser.getSuId()))
                .findAny()
                .orElseThrow(() -> new InvalidPermissionException(
                    "Invalid Permission in: " + ssId));

        //get suIdList
        Map<String, Object> hashMap = sisJoinCourseList
            .stream()
            .filter(sisJoinCourse -> sisJoinCourse.getJoinCourseType()
                .equals(SisJoinCourse.JoinCourseType.ATTENDANCE.ordinal()))
            .map(SisJoinCourse::getSuId)
            .collect(Collectors.toMap(Function.identity(),
                (s) -> Boolean.FALSE));
        if (hashMap.isEmpty())
            return new VoidOperationResponse(false, "No student in this " +
                "course");

        String key =
            String.format(signInKeyFormat, ssId,
                week);

        if (Boolean.TRUE.equals(sisRedisTemplate.hasKey(key)))
            return new VoidOperationResponse(false, "Sign in is processing");

        if (null == sisSchedule.getSlId())
            return new VoidOperationResponse(false,
                "该课程未指定地点，请联系管理员设置地点");

        SisLocation sisLocation =
            sisLocationMapper.selectByPrimaryKey(sisSchedule.getSlId());
        if (null == sisLocation ||
            null == sisLocation.getSlLat() ||
            null == sisLocation.getSlLong())
            return new VoidOperationResponse(false, "地点信息不全，请联系管理员");

        log.info("create signIn schedule: " + key);
        hashMap.put("loc_lat", sisLocation.getSlLat());
        hashMap.put("loc_long", sisLocation.getSlLong());
        if (log.isDebugEnabled()) {
            log.debug(String.format(
                "sign in schedule has location: key %s -> %s",
                key,
                new JSONObject(sisLocation)));
        }

        hashMap.put("create_time", currentDateTime);
        sisRedisTemplate.opsForHash().putAll(key, hashMap);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        threadPoolTaskScheduler.schedule(new EndSignInTask(key, ssId,
            week), calendar.toInstant());

        log.info("start signIn schedule and end at: " + calendar.toInstant().toString());
        return VoidSuccessOperationResponse.SUCCESS;
    }

    public OperationResponse<SisSignIn> getSignIn(Integer ssId, Integer week) {
        String key = String.format(signInKeyFormat, ssId, week);
        if (Boolean.TRUE.equals(sisRedisTemplate.hasKey(key))) {
            OperationResponse<SisSignIn> operationResponse =
                new OperationResponse<>();
            operationResponse.setSuccess(false);
            operationResponse.setCode(1);
            operationResponse.setMessage("Processing");
            return operationResponse;
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
                    OperationResponse<SisSignIn> operationResponse =
                        new OperationResponse<>();
                    operationResponse.setSuccess(false);
                    operationResponse.setCode(3);
                    operationResponse.setMessage("schedule error: " + ssId);
                    return operationResponse;
                }
                SisCourse sisCourse =
                    sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());

                SisSignInDetailExample sisSignInDetailExample =
                    new SisSignInDetailExample();
                sisSignInDetailExample.createCriteria().andSsiIdEqualTo(sisSignIn.getSsiId());
                List<SisSignInDetail> sisSignInDetailList =
                    sisSignInDetailMapper.selectByExample(sisSignInDetailExample);

                List<String> suIdList =
                    sisSignInDetailList.stream()
                        .map(SisSignInDetail::getSuId)
                        .distinct()
                        .collect(Collectors.toList());
                SisUserExample sisUserExample = new SisUserExample();
                sisUserExample.createCriteria().andSuIdIn(suIdList);
                List<SisUser> sisUserList = Optional
                    .of(sisUserMapper.selectByExample(sisUserExample))
                    .filter(l -> !l.isEmpty())
                    .orElse(new ArrayList<>());

                //merge
                sisSchedule.setSisCourse(sisCourse);

                sisSignInDetailList.forEach(d -> d.setSisUser(sisUserList.stream()
                    .filter(u -> u.getSuId().equals(d.getSuId()))
                    .peek(u -> u.setSuPassword(null))
                    .findAny()
                    .orElse(null)));

                sisSignIn.setSisSignInDetailList(sisSignInDetailList);
                sisSignIn.setSisSchedule(sisSchedule);

                OperationResponse<SisSignIn> operationResponse =
                    new OperationResponse<>();
                operationResponse.setSuccess(true);
                operationResponse.setCode(0);
                operationResponse.setData(sisSignIn);
                operationResponse.setMessage("End");
                return operationResponse;
            })
            .orElseGet(() -> {
                OperationResponse<SisSignIn> operationResponse =
                    new OperationResponse<>();
                operationResponse.setSuccess(false);
                operationResponse.setCode(2);
                operationResponse.setMessage("Not found");
                return operationResponse;
            });
    }

    public SisCourse getSignIns(SisUser sisUser, String scId) throws IncorrectParameterException {
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
        List<SisSignIn> sisSignInList = Optional.of(ssIdList)
            .filter(l -> !l.isEmpty())
            .map(l -> {
                SisSignInExample sisSignInExample = new SisSignInExample();
                sisSignInExample.createCriteria().andSsIdIn(l);
                return sisSignInMapper.selectByExample(sisSignInExample);
            })
            .orElse(new ArrayList<>());

        //join SignInDetail
        List<Integer> ssiIdList = sisSignInList.stream()
            .map(SisSignIn::getSsiId)
            .collect(Collectors.toList());
        List<SisSignInDetail> sisSignInDetailList = Optional.of(ssiIdList)
            .filter(l -> !l.isEmpty())
            .map(l -> {
                SisSignInDetailExample sisSignInDetailExample =
                    new SisSignInDetailExample();
                SisSignInDetailExample.Criteria criteria =
                    sisSignInDetailExample.createCriteria();
                criteria.andSsiIdIn(l);
                if (null != sisUser) criteria.andSuIdEqualTo(sisUser.getSuId());
                return sisSignInDetailMapper.selectByExample(sisSignInDetailExample);
            })
            .orElse(new ArrayList<>());

        //join student
        List<String> suIdList = sisSignInDetailList.stream()
            .map(SisSignInDetail::getSuId)
            .collect(Collectors.toList());
        List<SisUser> sisUserList = Optional.of(suIdList)
            .filter(l -> !l.isEmpty())
            .map(l -> {
                SisUserExample sisUserExample =
                    new SisUserExample();
                sisUserExample.createCriteria().andSuIdIn(l);
                return sisUserMapper.selectByExample(sisUserExample);
            })
            .orElse(new ArrayList<>());

        //merge student
        sisSignInDetailList.forEach(d -> d.setSisUser(sisUserList.stream()
            .filter(u -> u.getSuId().equals(d.getSuId()))
            .peek(u -> u.setSuPassword(null))
            .findAny()
            .orElse(null)));

        //merge signInDetail
        sisSignInList.forEach(i -> i.setSisSignInDetailList(
            sisSignInDetailList.stream()
                .filter(d -> d.getSsiId().equals(i.getSsiId()))
                .collect(Collectors.toList())));

        //merge schedule
        sisScheduleList.forEach(s -> {
            String keyPattern = String.format(signInKeyFormat,
                s.getSsId(), "*");

            // processing list
            List<SignInProcessing> processingList = Optional
                .ofNullable(sisRedisTemplate.keys(keyPattern))
                .map(p -> p.stream()
                    .map(key -> {
                        Matcher matcher = Pattern.compile(
                            "sis_ssId_\\d+_week_(\\d+)")
                            .matcher(key);
                        if (!matcher.find())
                            return null;

                        int week = Integer.valueOf(matcher.group(1));
                        return new SignInProcessing(s.getSsId(), week);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

            List<SisSignIn> tSignInList = sisSignInList.stream()
                .filter(i -> i.getSsId().equals(s.getSsId()))
                .collect(Collectors.toList());

            s.setSisProcessingList(processingList);
            s.setSisSignInList(tSignInList);
        });

        //merge course
        sisCourse.setSisScheduleList(sisScheduleList);

        return sisCourse;
    }

    public SisCourse getSignIns(String scId) throws IncorrectParameterException {
        return getSignIns(null, scId);
    }

    public Workbook exportSignIns(String scId) {
        HashMap<Integer, String> dayMap = new HashMap<Integer, String>() {{
            this.put(1, "一");
            this.put(2, "二");
            this.put(3, "三");
            this.put(4, "四");
            this.put(5, "五");
            this.put(6, "六");
            this.put(7, "日");
        }};
        HashMap<Integer, String> fortMap = new HashMap<Integer, String>() {{
            this.put(0, "全");
            this.put(1, "单");
            this.put(2, "双");
        }};
        String timeFormat = "%s [%s-%s] 星期%s 第 %s~%s 节 %s";

        SisCourse sisCourse = getSignIns(scId);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("学年度学期");
        row.createCell(1, CellType.STRING).setCellValue("课程序号");
        row.createCell(2, CellType.STRING).setCellValue("课程名字");
        row.createCell(3, CellType.STRING).setCellValue("上课时间");
        row.createCell(4, CellType.STRING).setCellValue("签到周");
        row.createCell(5, CellType.STRING).setCellValue("学号");
        row.createCell(6, CellType.STRING).setCellValue("姓名");
        row.createCell(7, CellType.STRING).setCellValue("签到状态");

        AtomicInteger rowIdx = new AtomicInteger(1);
        sisCourse.getSisScheduleList().forEach(s -> s.getSisSignInList().forEach(ssi -> ssi.getSisSignInDetailList().forEach(ssid -> {
            String schTime = String.format(timeFormat,
                fortMap.get(s.getSsFortnight()),
                s.getSsStartWeek(),
                s.getSsEndWeek(),
                dayMap.get(s.getSsDayOfWeek()),
                s.getSsStartTime(),
                s.getSsEndTime(),
                s.getSsRoom());

            Row tRow = sheet.createRow(rowIdx.getAndIncrement());
            tRow.createCell(0, CellType.STRING).setCellValue(s.getSsYearEtTerm());
            tRow.createCell(1, CellType.STRING).setCellValue(sisCourse.getScId());
            tRow.createCell(2, CellType.STRING).setCellValue(sisCourse.getScName());
            tRow.createCell(3, CellType.STRING).setCellValue(schTime);
            tRow.createCell(4, CellType.STRING).setCellValue(ssi.getSsiWeek());
            tRow.createCell(5, CellType.STRING).setCellValue(ssid.getSuId());
            tRow.createCell(6, CellType.STRING).setCellValue(ssid.getSisUser().getSuName());
            tRow.createCell(7, CellType.STRING).setCellValue(ssid.getSsidStatus() ? "已签到" : "缺勤");
        })));
        return workbook;
    }

    @SuppressWarnings("ConstantConditions")
    public VoidOperationResponse signIn(SisUser sisUser,
                                        Integer ssId,
                                        LocalDateTime currentDateTime,
                                        JSONObject locationJson) throws InvalidTimeParameterException, IncorrectParameterException, InvalidPermissionException {
        if (log.isDebugEnabled())
            log.debug("User " + sisUser.getSuId() + " try to signIn");

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
        if (until > 10 || until < 0)
            return new VoidOperationResponse(false,
                "Error time until: " + until);

        if (sisRedisTemplate.opsForHash().hasKey(key, "loc_lat") &&
            sisRedisTemplate.opsForHash().hasKey(key, "loc_long")) {
            Double stdLocLat = (Double) sisRedisTemplate.opsForHash().get(key,
                "loc_lat");
            Double stdLocLong = (Double) sisRedisTemplate.opsForHash().get(key,
                "loc_long");
            double locLat = locationJson.getDouble("loc_lat");
            double locLong = locationJson.getDouble("loc_long");

            double distance = LocationUtil.getDistance(stdLocLat, stdLocLong,
                locLat, locLong);
            if (distance > MAX_DISTANCE) return new VoidOperationResponse(false,
                "Error location distance: " + distance);
        }

        sisRedisTemplate.opsForHash().put(key, sisUser.getSuId(), true);
        if (log.isDebugEnabled())
            log.debug("User " + sisUser.getSuId() + " successfully signIn");

        return VoidSuccessOperationResponse.SUCCESS;
    }

    public List<SisSignInDetail> getStuSignIns(String suId) {
        SisSignInDetailExample sisSignInDetailExample =
            new SisSignInDetailExample();
        sisSignInDetailExample.createCriteria().andSuIdEqualTo(suId);
        List<SisSignInDetail> sisSignInDetailList =
            sisSignInDetailMapper.selectByExample(sisSignInDetailExample);
        if (sisSignInDetailList.isEmpty()) return sisSignInDetailList;

        //join sisSignIn
        List<Integer> ssiIdList = sisSignInDetailList.stream()
            .map(SisSignInDetail::getSsiId)
            .distinct()
            .collect(Collectors.toList());
        List<SisSignIn> sisSignInList = Optional
            .of(ssiIdList)
            .filter(l -> !l.isEmpty())
            .map(l -> {
                SisSignInExample sisSignInExample = new SisSignInExample();
                sisSignInExample.createCriteria().andSsiIdIn(l);
                return sisSignInMapper.selectByExample(sisSignInExample);
            })
            .orElse(new ArrayList<>());


        //join schedule
        List<Integer> ssIdList = sisSignInList.stream()
            .map(SisSignIn::getSsId)
            .distinct()
            .collect(Collectors.toList());
        List<SisSchedule> sisScheduleList = Optional
            .of(ssIdList)
            .filter(l -> !l.isEmpty())
            .map(l -> {
                SisScheduleExample sisScheduleExample =
                    new SisScheduleExample();
                sisScheduleExample.createCriteria().andSsIdIn(l);
                return sisScheduleMapper.selectByExample(sisScheduleExample);
            })
            .orElse(new ArrayList<>());

        //join course
        List<String> scIdList = sisScheduleList.stream()
            .map(SisSchedule::getScId)
            .distinct()
            .collect(Collectors.toList());
        List<SisCourse> sisCourseList = Optional
            .of(scIdList)
            .filter(l -> !l.isEmpty())
            .map(l -> {
                SisCourseExample sisCourseExample = new SisCourseExample();
                sisCourseExample.createCriteria().andScIdIn(l);
                return sisCourseMapper.selectByExample(sisCourseExample);
            })
            .orElse(new ArrayList<>());

        sisSignInDetailList
            .forEach(d -> {
                SisSignIn sisSignIn =
                    sisSignInList.stream().filter(s -> s.getSsiId().equals(d.getSsiId())).findAny().orElse(null);
                if (null == sisSignIn)
                    return;

                SisSchedule sisSchedule =
                    sisScheduleList.stream().filter(s -> s.getSsId().equals(sisSignIn.getSsId())).findAny().orElse(null);
                if (null == sisSchedule)
                    return;

                SisCourse sisCourse =
                    sisCourseList.stream().filter(c -> c.getScId().equals(sisSchedule.getScId())).findAny().orElse(null);
                if (null == sisCourse)
                    return;

                d.setSisSignIn(sisSignIn);
                sisSignIn.setSisSchedule(sisSchedule);
                sisSchedule.setSisCourse(sisCourse);
            });

        return sisSignInDetailList;
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
            if (null == sisRedisTemplate.hasKey(key) || !sisRedisTemplate.hasKey(key))
                return;
            Map<Object, Object> map =
                sisRedisTemplate.opsForHash().entries(key);

            map.remove("create_time");
            map.remove("loc_lat");
            map.remove("loc_long");

            //create signInDetail list
            List<SisSignInDetail> sisSignInDetailList = map
                .entrySet()
                .stream()
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
            sisSignInMapper.insert(sisSignIn);

            //insert signInDetail
            sisSignInDetailList
                .forEach(sisSignInDetail -> sisSignInDetail.setSsiId(sisSignIn.getSsiId()));
            sisSignInDetailMapper.insertList(sisSignInDetailList);

            //finish
            sisRedisTemplate.delete(key);
            log.info("start update scAttRate: by schedule " + ssId);
            supervisionAspect.updateAttRate(ssId);
        }
    }
}