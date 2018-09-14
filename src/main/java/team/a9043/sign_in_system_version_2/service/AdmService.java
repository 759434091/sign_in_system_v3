package team.a9043.sign_in_system_version_2.service;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.mapper.AdmMapper;
import team.a9043.sign_in_system_version_2.mapper.CourseMapper;
import team.a9043.sign_in_system_version_2.mapper.SupervisionMapper;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithHistorySignIn;
import team.a9043.sign_in_system_version_2.pojo.extend.StuTimetable;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;
import team.a9043.sign_in_system_version_2.util.TransSchedule;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("admService")
public class AdmService {
    private final Logger logger = LoggerFactory.getLogger(AdmService.class);
    static final Map<String, Map> SIGN_IN_MAP = new HashMap<String, Map>() {{
        Map<String, Object> presentMap = new HashMap<String, Object>() {{
            this.put("msg", "Present");
            this.put("status", 0);
        }};
        Map<String, Object> absentMap = new HashMap<String, Object>() {{
            this.put("msg", "Absent");
            this.put("status", 1);
        }};
        Map<String, Object> noTimeMap = new HashMap<String, Object>() {{
            this.put("msg", "Time has not arrive");
            this.put("status", 2);
        }};
        Map<String, Object> noApproveMap = new HashMap<String, Object>() {{
            this.put("msg", "Leave is not approved");
            this.put("status", 3);
        }};
        Map<String, Object> approveMap = new HashMap<String, Object>() {{
            this.put("msg", "Leave approved");
            this.put("status", 4);
        }};
        this.put("0_0_0", absentMap);
        this.put("0_0_1", noTimeMap);
        this.put("1_0_0", presentMap);
        this.put("1_0_1", presentMap);
        this.put("1_1_0", noApproveMap);
        this.put("1_1_1", approveMap);
    }};
    private final TransSchedule transSchedule;
    private final RedisTemplate redisTemplate;
    private final ImportService importService;
    @Value("#{${cozMap}}")
    private HashMap<String, String> COZ_TABLE_MAP;
    @Resource
    private AdmMapper admMapper;
    @Resource
    private SupervisionMapper supervisionMapper;
    @Resource
    private CourseMapper courseMapper;

    @Autowired
    public AdmService(TransSchedule transSchedule, RedisTemplate redisTemplate, ImportService importService) {
        this.redisTemplate = redisTemplate;
        this.importService = importService;
        this.transSchedule = transSchedule;
    }

    public List<User> getSupervisor() {
        return admMapper.getSupervisor();
    }

    /**
     * 一周督导列表
     *
     * @param currentDateTime 当前时间
     * @return 督导课城列表
     */
    public List<Supervision> getSupervision(LocalDateTime currentDateTime, Integer suvWeek) {
        return supervisionMapper.getSuvCourseList(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                suvWeek,
                true,
                null);
    }

    public SuvRecWithSuv getSuvRec(LocalDateTime currentDateTime, Supervision supervision) throws ParameterNotFoundException {
        return supervisionMapper.getSuvRec(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                Optional
                        .ofNullable(supervision.getSuvId())
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect suvId."))
        );
    }

    public StuTimetable getStuTimetable(String usrId) throws ParameterNotFoundException {
        return courseMapper.getStuTimetable(
                Optional
                        .ofNullable(usrId)
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect usrId.")));
    }

    @Transactional
    public boolean grantSupervisor(String usrId) throws ParameterNotFoundException {
        return admMapper.modifySupervisor(true,
                Optional
                        .ofNullable(usrId)
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect usrId."))) > 0;
    }

    @Transactional
    public boolean revokeSupervisor(String usrId) throws ParameterNotFoundException {
        return admMapper.modifySupervisor(false,
                Optional
                        .ofNullable(usrId)
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect usrId."))) > 0;
    }

    public int getCourseCount(String cozName, Integer grade, String depName) {
        return admMapper.getCourseCount(
                getFuzzySearch(cozName),
                Optional
                        .ofNullable(grade)
                        .orElse(null),
                getFuzzySearch(depName));
    }

    public List<Course> getCourse(String cozName, Integer page, Integer pageSize, String sortStr, Boolean isAsc, Integer grade, String depName) {
        return admMapper.getCourse(
                getFuzzySearch(cozName),
                Optional
                        .ofNullable(page)
                        .filter(pg -> pg > 0)
                        .map(pg -> (pg - 1) * Optional.ofNullable(pageSize).orElse(8))
                        .orElse(0),
                Optional
                        .ofNullable(pageSize)
                        .orElse(8),
                COZ_TABLE_MAP.get(sortStr),
                Optional
                        .ofNullable(isAsc)
                        .orElse(true),
                Optional
                        .ofNullable(grade)
                        .orElse(null),
                getFuzzySearch(depName));
    }

    private String getFuzzySearch(String fuzzyName) {
        return Optional
                .ofNullable(fuzzyName)
                .filter(name -> !name.equals(""))
                .map((name) -> {
                    StringBuilder cozSearchBuilder = new StringBuilder();
                    Arrays.stream(name.split("")).forEach(ch -> cozSearchBuilder.append("%").append(ch));
                    cozSearchBuilder.append("%");
                    return cozSearchBuilder.toString();
                })
                .orElse(null);
    }

    public List<User> getCozStudent(String cozId) throws ParameterNotFoundException {
        return admMapper.getCozStudent(Optional
                .ofNullable(cozId)
                .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect cozId.")));
    }

    public List<SuvRecWithSuv> getCozSuvRec(String cozId, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        return admMapper.getCozSuvRec(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                Optional
                        .ofNullable(cozId)
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect cozId.")));
    }

    public JSONArray getCozHistorySignIn(String cozId, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        //获取签到列表
        List<ScheduleWithHistorySignIn> scheduleWithHistorySignInList = courseMapper.getHistorySignIn(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                Optional
                        .ofNullable(cozId)
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect cozId.")),
                null
        );
        //获得课堂学生
        Map<String, JSONObject> studentMap = admMapper.getCozStudent(cozId).stream().collect(Collectors.toMap(User::getUsrId, JSONObject::new));

        //格式换换成JSON
        JSONArray jSchWsiList = new JSONArray(scheduleWithHistorySignInList);
        jSchWsiList.forEach((jSchWsiObj) -> {
            JSONObject jSchWsi = (JSONObject) jSchWsiObj;
            JSONArray signInList = jSchWsi.getJSONArray("signInList");

            signInList.forEach((signInObj) -> {
                JSONObject signIn = (JSONObject) signInObj;
                JSONArray signInRecList = signIn.getJSONArray("signInRecList");

                //signInRecMap
                Map<String, JSONObject> signInRecMap = new HashMap<>();
                //处理签到人员
                signInRecList
                        .forEach(sir -> {
                            JSONObject signInRec = (JSONObject) sir;

                            //状态码处理
                            String usrId = signInRec.getString("userUsrId");
                            String key = "1_" + (signInRec.getBoolean("sirLeave") ? 1 : 0) + "_" + signInRec.getInt("sirApprove");
                            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get(key).get("msg"));
                            signInRec.put("signInStatusCode", SIGN_IN_MAP.get(key).get("status"));
                            signInRec.put("user", studentMap.get(usrId));

                            //开始放入Map
                            JSONObject signInRecInMap = signInRecMap.get(usrId);
                            //Map无记录
                            if (signInRecInMap == null) {
                                signInRecMap.put(usrId, signInRec);
                                return;
                            }
                            //有签到记录
                            if (!signInRecInMap.get("signInStatusCode").equals(0) && signInRec.get("signInStatusCode").equals(0)) {
                                signInRecMap.put(usrId, signInRec);
                                return;
                            }
                            //有请假通过记录
                            if (signInRecInMap.get("signInStatusCode").equals(3) && signInRec.get("signInStatusCode").equals(4)) {
                                signInRecMap.put(usrId, signInRec);
                            }
                        });
                //处理未签到人员
                studentMap
                        .values()
                        .stream()
                        .filter(student -> signInRecMap.get(student.getString("usrId")) == null)
                        .forEach(student -> {
                            JSONObject signInRec = new JSONObject();
                            signInRec.put("user", studentMap.get(student.getString("usrId")));
                            addMsgForNotSignIn(currentDateTime,
                                    jSchWsi.getInt("schStartTime"),
                                    jSchWsi.getInt("schDay"),
                                    signIn.getInt("siWeek"),
                                    signIn.getBoolean("siAuto"),
                                    LocalDateTime.parse(signIn.getString("siTime").replace(" ", "T")),
                                    signInRec,
                                    transSchedule);
                            signInRecMap.put(student.getString("usrId"), signInRec);
                        });
                //更新signInRecList
                signIn.put("signInRecList", signInRecMap.values());
            });
        });
        return jSchWsiList;
    }

    static void addMsgForNotSignIn(LocalDateTime currentDateTime, Integer schStartTime, Integer schDay, Integer siWeek, Boolean siAuto, LocalDateTime siTime, JSONObject signInRec, TransSchedule transSchedule) {
        //比较周
        if (transSchedule.getWeek(currentDateTime) > siWeek) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_0").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_0").get("status"));
            return;
        } else if (transSchedule.getWeek(currentDateTime) < siWeek) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_1").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_1").get("status"));
            return;
        }

        //比较星期
        if (transSchedule.getDay(currentDateTime) > schDay) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_0").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_0").get("status"));
            return;
        } else if (transSchedule.getDay(currentDateTime) < schDay) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_1").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_1").get("status"));
            return;
        }

        //比较节数
        if (transSchedule.getTime(currentDateTime) > schStartTime) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_0").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_0").get("status"));
            return;
        } else if (transSchedule.getTime(currentDateTime) < schStartTime) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_1").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_1").get("status"));
            return;
        }

        //比较默认签到时间
        boolean autoAfter = siAuto
                && transSchedule.getTime(schStartTime).isBefore(currentDateTime.toLocalTime());
        //比较人工设置时间
        boolean manAfter = !siTime.isEqual(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0, 0))
                && siTime.toLocalTime().isBefore(currentDateTime.toLocalTime());
        if (autoAfter && manAfter) {
            signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_0").get("msg"));
            signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_0").get("status"));
            return;
        }
        signInRec.put("signInStatusMsg", SIGN_IN_MAP.get("0_0_1").get("msg"));
        signInRec.put("signInStatusCode", SIGN_IN_MAP.get("0_0_1").get("status"));
    }

    public boolean insertSupervision(List<Supervision> supervisionList, LocalDateTime currentDateTime) {
        if (supervisionList.size() <= 0) {
            return false;
        }
        List<Supervision> oldSupervisionList = admMapper.selectSupervision(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                supervisionList);
        List<Supervision> finalSupervisionList = supervisionList.stream()
                .filter(supervision -> oldSupervisionList.stream().noneMatch(oldSupervision -> oldSupervision.getScheduleSchId().equals(supervision.getScheduleWithCozDtl().getSchId()) && oldSupervision.getSuvWeek().equals(supervision.getSuvWeek())))
                .collect(Collectors.toList());
        return admMapper.insertSupervision(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                finalSupervisionList
        ) > 0;
    }

    public List<Integer> getCourseSupervision(String cozId, Integer week, LocalDateTime currentDateTime) {
        return admMapper.getCourseSupervision(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                cozId,
                week);
    }

    public boolean deleteSupervision(Supervision supervision, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        return admMapper.deleteSupervision(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                Optional
                        .ofNullable(supervision.getSuvId())
                        .orElseThrow(() -> new ParameterNotFoundException("Parameter not found! Expect suvId."))
        ) > 0;
    }

    public SignIn getSignInBySchAndWeek(Supervision supervision, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        try {
            return supervisionMapper.getSignInBySchAndWeek(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    supervision.getSuvWeek(),
                    supervision.getScheduleWithCozDtl().getSchId());
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect suvWeek, scheduleSchId.");
        }
    }

    public JSONArray getSignInByWeek(Integer siWeek, LocalDateTime currentDateTime) {
        // 获得基本信息
        List<SignIn> signInRawList = admMapper.getSignInRaw(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                siWeek);
        Set<Integer> schIdSet = signInRawList.stream().map(SignIn::getScheduleSchId).collect(Collectors.toSet());
        Set<Integer> siIdSet = signInRawList.stream().map(SignIn::getSiId).collect(Collectors.toSet());
        if (schIdSet.size() <= 0 || siIdSet.size() <= 0) {
            return new JSONArray();
        }
        List<ScheduleWithCozDtl> scheduleWithCozDtl = admMapper.getScheduleWithCozDtl(schIdSet);
        List<SignInRec> signInRecList = admMapper.getSignInRecRaw(
                transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                siIdSet);
        Set<String> cozIdSet = scheduleWithCozDtl.stream().map(Schedule::getCourseCozId).collect(Collectors.toSet());
        if (cozIdSet.size() <= 0) {
            return new JSONArray();
        }
        List<Attendance> attendanceList = admMapper.getSignInAttendance(cozIdSet);
        Set<User> userSet = attendanceList.stream().map(Attendance::getUserList).flatMap(Collection::stream).collect(Collectors.toSet());

        // 格式转换
        JSONArray jsonSignInList = new JSONArray(signInRawList);
        JSONArray jsonSignInRecList = new JSONArray(signInRecList);

        // 集合学生
        jsonSignInRecList.forEach(jsonSignInRecObj -> {
            JSONObject jsonSignInRec = (JSONObject) jsonSignInRecObj;
            List<User> thisStudent = userSet.stream().filter(user -> user.getUsrId().equals(jsonSignInRec.getString("userUsrId"))).limit(1).collect(Collectors.toList());
            if (thisStudent.size() > 0) {
                jsonSignInRec.put("user", new JSONObject(thisStudent.get(0)));
            }
        });

        // 集合课程记录
        jsonSignInList.forEach(jsonSignInObj -> {
            JSONObject jsonSignIn = (JSONObject) jsonSignInObj;
            List<ScheduleWithCozDtl> thisScheduleWithCozDtl = scheduleWithCozDtl.stream()
                    .filter(schedule -> schedule.getSchId().equals(jsonSignIn.getInt("scheduleSchId")))
                    .limit(1)
                    .collect(Collectors.toList());
            if (thisScheduleWithCozDtl.size() > 0) {
                jsonSignIn.put("scheduleWithCozDtl", new JSONObject(thisScheduleWithCozDtl.get(0)));
            }
        });

        // 集合签到记录
        jsonSignInList.forEach(jsonSignInObj -> {
            // 集合
            JSONObject jsonSignIn = (JSONObject) jsonSignInObj;
            JSONArray thisSignInRecList = new JSONArray();
            Map<String, JSONObject> signInRecMap = new HashMap<>();
            jsonSignInRecList.forEach(jsonSignInRecObj -> {
                JSONObject jsonSignInRec = (JSONObject) jsonSignInRecObj;
                if (jsonSignInRec.getInt("signInSiId") == jsonSignIn.getInt("siId")) {

                    thisSignInRecList.put(jsonSignInRec);
                }
            });

            //处理签到人员
            thisSignInRecList
                    .forEach(sir -> {
                        JSONObject signInRec = (JSONObject) sir;

                        //状态码处理
                        String usrId = signInRec.getString("userUsrId");
                        String key = "1_" + (signInRec.getBoolean("sirLeave") ? 1 : 0) + "_" + signInRec.getInt("sirApprove");
                        signInRec.put("signInStatusMsg", SIGN_IN_MAP.get(key).get("msg"));
                        signInRec.put("signInStatusCode", SIGN_IN_MAP.get(key).get("status"));

                        //开始放入Map
                        JSONObject signInRecInMap = signInRecMap.get(usrId);
                        //Map无记录
                        if (signInRecInMap == null) {
                            signInRecMap.put(usrId, signInRec);
                            return;
                        }
                        //有签到记录
                        if (!signInRecInMap.get("signInStatusCode").equals(0) && signInRec.get("signInStatusCode").equals(0)) {
                            signInRecMap.put(usrId, signInRec);
                            return;
                        }
                        //有请假通过记录
                        if (signInRecInMap.get("signInStatusCode").equals(3) && signInRec.get("signInStatusCode").equals(4)) {
                            signInRecMap.put(usrId, signInRec);
                        }
                    });
            //处理未签到人员
            attendanceList.stream()
                    .filter(attendance -> attendance.getCourseCozId().equals(jsonSignIn.getJSONObject("scheduleWithCozDtl").getString("courseCozId")))
                    .limit(1)
                    .forEach(attendance -> attendance.getUserList().stream()
                            .filter(student -> signInRecMap.get(student.getUsrId()) == null)
                            .forEach(student -> {
                                JSONObject signInRec = new JSONObject();
                                signInRec.put("user", new JSONObject(student));
                                addMsgForNotSignIn(currentDateTime,
                                        jsonSignIn.getJSONObject("scheduleWithCozDtl").getInt("schStartTime"),
                                        jsonSignIn.getJSONObject("scheduleWithCozDtl").getInt("schDay"),
                                        jsonSignIn.getInt("siWeek"),
                                        jsonSignIn.getBoolean("siAuto"),
                                        LocalDateTime.parse(jsonSignIn.getString("siTime").replace(" ", "T")),
                                        signInRec,
                                        transSchedule);

                                signInRecMap.put(student.getUsrId(), signInRec);
                            }));


            jsonSignIn.put("signInRecList", signInRecMap.values());
        });

        return jsonSignInList;
    }

    @Transactional
    public boolean changeSignIn(SignIn signIn, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        try {
            SignIn dataBaseSignIn = supervisionMapper.getSignInBySchAndWeek(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    signIn.getSiWeek(),
                    signIn.getScheduleSchId());
            //数据库无记录
            if (dataBaseSignIn == null) {
                if (!signIn.getSiAuto() && signIn.getSiTime().isEqual(LocalDateTime.of(1970, 1, 1, 0, 0, 0))) {
                    return true;
                }
                return supervisionMapper.insertSignIn(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        signIn.getSiWeek(),
                        signIn.getSiTime() == null ? LocalDateTime.of(1970, 1, 1, 0, 0, 0) : signIn.getSiTime(),
                        signIn.getSiAuto() == null ? true : signIn.getSiAuto(),
                        signIn.getScheduleSchId()) > 0;
            }

            //请求删除
            if (!signIn.getSiAuto() && signIn.getSiTime().isEqual(LocalDateTime.of(1970, 1, 1, 0, 0, 0))) {
                return supervisionMapper.deleteSignIn(
                        transSchedule.getYear(currentDateTime),
                        transSchedule.getTerm(currentDateTime),
                        signIn.getSiWeek(),
                        signIn.getScheduleSchId()) >= 0;
            }

            //请求更新
            return supervisionMapper.updateSignIn(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    signIn.getSiWeek(),
                    signIn.getSiTime() == null ? LocalDateTime.of(1970, 1, 1, 0, 0, 0) : signIn.getSiTime(),
                    signIn.getSiAuto() == null ? true : signIn.getSiAuto(),
                    signIn.getScheduleSchId()) > 0;
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect object signIn");
        }
    }

    @Async
    @Transactional
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void readCozExcel(File file, String taskKey) {
        final String[] rowNameList = {"课程序号", "课程名称", "教师工号", "授课教师", "学年度学期", "上课地点", "上课时间", "年级", "上课院系", "实际人数", "容量"};
        HashMap<String, Integer> cozMap = new HashMap<>();
        ArrayList<ArrayList<Object>> result;

        // check file
        if (file == null) {
            logger.error("文件不存在");
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            return;
        }

        // read excel
        result = readExcel(file);
        if (checkInvalid(result, rowNameList, cozMap)) {
            logger.error("文件不合法");
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            return;
        }

        // Base
        importService.addTeacher(result, cozMap);
        redisTemplate.opsForValue().set(taskKey, 20, 5, TimeUnit.MINUTES);
        importService.addLocation(result, cozMap);
        redisTemplate.opsForValue().set(taskKey, 30, 5, TimeUnit.MINUTES);
        importService.addDepartment(result, cozMap);
        redisTemplate.opsForValue().set(taskKey, 40, 5, TimeUnit.MINUTES);
        List<String> cozOldList = importService.addCourse(result, cozMap);
        redisTemplate.opsForValue().set(taskKey, 50, 5, TimeUnit.MINUTES);

        // Association
        importService.addCozTea(result, cozMap, cozOldList);
        redisTemplate.opsForValue().set(taskKey, 60, 5, TimeUnit.MINUTES);
        importService.addCozDep(result, cozMap, cozOldList);
        redisTemplate.opsForValue().set(taskKey, 70, 5, TimeUnit.MINUTES);
        importService.addCozSch(result, cozMap, cozOldList);
        redisTemplate.opsForValue().set(taskKey, 100, 5, TimeUnit.MINUTES);
    }

    private boolean checkInvalid(ArrayList<ArrayList<Object>> result, String[] rowNameList, HashMap<String, Integer> map) {
        if (result == null || result.size() <= 1) {
            return true;
        }

        ArrayList<Object> row = result.get(0);
        int mapIndex;
        for (String rowName : rowNameList) {
            mapIndex = row.indexOf(rowName);
            if (mapIndex < 0) {
                return true;
            }
            map.put(rowName, mapIndex);
        }
        return false;
    }

    @Async
    @Transactional
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void readStuExcel(File file, String taskKey) {
        final String[] rowNameList = {"学号", "姓名", "课程序号"};
        ArrayList<ArrayList<Object>> result;
        HashMap<String, Integer> stuMap = new HashMap<>();

        // check file
        if (file == null) {
            logger.error("文件不存在");
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            return;
        }

        // read excel
        result = readExcel(file);
        if (checkInvalid(result, rowNameList, stuMap)) {
            logger.error("文件不合法");
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            return;
        }
        redisTemplate.opsForValue().set(taskKey, 30, 5, TimeUnit.MINUTES);

        // insert student
        Set<String> usrIdSet = importService.addStudent(result, stuMap);
        redisTemplate.opsForValue().set(taskKey, 60, 5, TimeUnit.MINUTES);

        // insert attendance
        importService.addAttendance(result, stuMap, usrIdSet);
        redisTemplate.opsForValue().set(taskKey, 100, 5, TimeUnit.MINUTES);
    }

    private ArrayList<ArrayList<Object>> readExcel(File file) {
        //默认单元格内容为数字时格式
        DecimalFormat df = new DecimalFormat("0");
        // 默认单元格格式化日期字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化数字
        DecimalFormat nf = new DecimalFormat("0.000");
        try {
            ArrayList<ArrayList<Object>> rowList = new ArrayList<>();
            ArrayList<Object> colList;
            Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            Cell cell;
            Object value;
            for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                colList = new ArrayList<>();
                if (row == null) {
                    //当读取行为空时
                    if (i != sheet.getPhysicalNumberOfRows()) {
                        //判断是否是最后一行
                        rowList.add(colList);
                    }
                    continue;
                } else {
                    rowCount++;
                }
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
                        //当该单元格为空
                        if (j != row.getLastCellNum()) {//判断是否是该行中最后一个单元格
                            colList.add("");
                        }
                        continue;
                    }
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            value = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            if ("@".equals(cell.getCellStyle().getDataFormatString()) ||
                                    (Double.parseDouble(Math.round(cell.getNumericCellValue()) + ".0") == cell.getNumericCellValue())) {
                                value = df.format(cell.getNumericCellValue());
                            } else if ("General".equals(cell.getCellStyle()
                                    .getDataFormatString())) {
                                value = nf.format(cell.getNumericCellValue());
                            } else {
                                value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                        .getNumericCellValue()));
                            }
                            break;
                        case BOOLEAN:
                            value = cell.getBooleanCellValue();
                            break;
                        case BLANK:
                            value = "";
                            break;
                        default:
                            value = cell.toString();
                    }
                    colList.add(value);
                }
                rowList.add(colList);
            }
            return rowList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> searchDepartment(String depStr) {
        return admMapper.searchDepartment(getFuzzySearch(depStr));
    }

    public JSONObject getStaAttRate() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("avgAttRate", admMapper.getStaAttRate().get("avgAttRate"));
        return jsonObject;
    }

    public JSONObject getAbsRankList(Integer limitNum) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("absRankList", admMapper.getAbsRankList(Optional.ofNullable(limitNum).orElse(20)));
        return jsonObject;
    }
}
