package team.a9043.sign_in_system_version_2.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.mapper.CourseMapper;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithHistorySignIn;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.StuTimetable;
import team.a9043.sign_in_system_version_2.redisDao.SignInByRedis;
import team.a9043.sign_in_system_version_2.util.LocationUtil;
import team.a9043.sign_in_system_version_2.util.TransSchedule;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Service("stuService")
public class StuService{
    @Resource
    private CourseMapper courseMapper;
    private final SignInByRedis signInByRedis;
    private final TransSchedule transSchedule;
    @Value("${judge-location.max-distance}")
    private int MAX_DISTANCE;

    @Autowired
    public StuService(SignInByRedis signInByRedis, TransSchedule transSchedule) {
        this.signInByRedis = signInByRedis;
        this.transSchedule = transSchedule;
    }

    public StuTimetable getStuTimetable(User user) {
        return courseMapper.getStuTimetable(user.getUsrId());
    }

    /**
     * 获得本周需要签到的课程
     *
     * @param user            用户
     * @param currentDateTime 当前时间
     * @return 签到带课程
     */
    public List<SignInWithCozDtl> getNeedSignInByUser(User user, LocalDateTime currentDateTime) {
        List<SignInWithCozDtl> signInWithCozDtlList = courseMapper.getNeedSignInByUser(transSchedule.getYear(currentDateTime),
                transSchedule.getTerm(currentDateTime),
                user.getUsrId());

        signInWithCozDtlList.forEach(signInWithCozDtl -> {
            //年&学期&周不等直接移出
            if (!signInWithCozDtl.getSiWeek().equals(transSchedule.getWeek(currentDateTime)) ||
                    !signInWithCozDtl.getScheduleWithCozDtl().getSchYear().equals(transSchedule.getYear(currentDateTime)) ||
                    !signInWithCozDtl.getScheduleWithCozDtl().getSchTerm().equals(transSchedule.getTerm(currentDateTime))) {
                signInWithCozDtlList.remove(signInWithCozDtl);
                return;
            }
            // (自动关 + 时间已过) * (人工关 + 时间已过)
            if ((!signInWithCozDtl.getSiAuto() ||
                    transSchedule
                            .getTime(signInWithCozDtl.getScheduleWithCozDtl().getSchStartTime())
                            .plus(10, ChronoUnit.MINUTES)
                            .isAfter(currentDateTime.toLocalTime()))
                    &&
                    (signInWithCozDtl
                            .getSiTime()
                            .isEqual(LocalDateTime.of(1970, 1, 1, 0, 0, 0)) ||
                            signInWithCozDtl
                                    .getSiTime()
                                    .plus(10, ChronoUnit.MINUTES)
                                    .isAfter(currentDateTime))) {
                signInWithCozDtlList.remove(signInWithCozDtl);
            }
        });
        return signInWithCozDtlList;
    }

    public JSONObject signIn(User student, Schedule schedule, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        try {

            /*
             ******************************
             * 错误检测
             ******************************/
            //通过主键获得标准schedule
            Schedule standardSchedule = courseMapper.getScheduleByPrimaryKey(schedule);
            if (standardSchedule == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("err", "Schedule not found!");
                return jsonObject;
            }

            //通过week&Sch获得标准standardSignIn
            SignIn standardSignIn = courseMapper.getSignInByWeekAndSch(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    standardSchedule.getSchId(),
                    transSchedule.getWeek(currentDateTime));
            if (standardSignIn == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("needSignIn", false);
                return jsonObject;
            }

            /*
             ******************************
             * 开始签到
             ******************************/
            try {
                boolean finalRes = false;
                //从缓存是否签到 40分钟有效
                String key = "signIn_" + standardSignIn.getSiId() + "_" + student.getUsrId();
                boolean isSignInRedis = signInByRedis.isSignIn(key);
                if (isSignInRedis) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("err", "Already sign in!");
                    return jsonObject;
                }

                //判断地点
                Location standardLocation = standardSchedule.getLocation();
                Location stuLocation = schedule.getLocation();
                boolean locRes = judgeLocation(standardLocation.getLocLat(), standardLocation.getLocLong(), stuLocation.getLocLat(), stuLocation.getLocLong());
                //判断默认签到时间
                boolean defaultTimeRes = isDefaultTimeRes(transSchedule.getTime(standardSchedule.getSchStartTime()), currentDateTime.toLocalTime(), standardSignIn);
                //判断人工发起签到时间
                boolean manuelTimeRes = isManualTimeRes(standardSignIn, currentDateTime.toLocalTime());

                // (自动通过 + 人工通过) * 地点通过
                if ((defaultTimeRes || manuelTimeRes) && locRes) {
                    finalRes = signInByRedis.insertSignIn(student.getUsrId(), standardSignIn.getSiId(), currentDateTime);
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", finalRes);
                jsonObject.put("status", finalRes ? "success" : "error");
                jsonObject.put("timeCheck", defaultTimeRes || manuelTimeRes);
                jsonObject.put("locCheck", locRes);
                jsonObject.put("needSignIn", true);
                return jsonObject;
            } catch (NullPointerException e) {
                e.printStackTrace();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("err", "Parameters error! " + e.getLocalizedMessage());
                return jsonObject;
            }
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect schId, schSignInWeek, location");
        }
    }

    @Transactional
    public JSONObject leave(User student, MultipartFile voucher, Schedule schedule, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        try {
            /*
             ******************************
             * 错误检测
             ******************************/
            //通过主键获得标准schedule
            Schedule standardSchedule = courseMapper.getScheduleByPrimaryKey(schedule);
            if (standardSchedule == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("err", "Schedule not found!");
                return jsonObject;
            }

            //通过tempSignIn获得标准standardSignIn
            SignIn standardSignIn = courseMapper.getSignInByWeekAndSch(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    standardSchedule.getSchId(),
                    transSchedule.getWeek(currentDateTime));
            if (standardSignIn == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("needSignIn", false);
                return jsonObject;
            }

            /*
             ******************************
             * 开始请假
             ******************************/
            try {
                //从缓存判断是否签到 40分钟有效
                String key = "signIn_" + student.getUsrId() + "_" + standardSchedule.getSchId() + "_" + schedule.getSchSignInWeek();
                boolean isSignInRedis = signInByRedis.isSignIn(key);
                if (isSignInRedis) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("err", "Already sign in!");
                    return jsonObject;
                }

                //开始请假
                try {
                    boolean res = courseMapper.stuLeave(
                            transSchedule.getYear(currentDateTime),
                            transSchedule.getTerm(currentDateTime),
                            student.getUsrId(),
                            standardSignIn.getSiId(),
                            voucher == null ? null : voucher.getBytes(),
                            currentDateTime);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("success", res);
                    jsonObject.put("status", res ? "success" : "error");
                    return jsonObject;
                } catch (IOException e) {
                    e.printStackTrace();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("err", "Voucher file error! " + e.getLocalizedMessage());
                    return jsonObject;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("err", "Parameters error! " + e.getLocalizedMessage());
                return jsonObject;
            }
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect schId, schSignInWeek, voucher");
        }
    }

    /**
     * 获得历史签到/请假/缺勤记录
     *
     * @param student         学生
     * @param course          课程
     * @param currentDateTime 当前时间
     * @return 记录数组
     * @throws ParameterNotFoundException 参数不足
     */
    public JSONArray getHistorySignIn(User student, Course course, LocalDateTime currentDateTime) throws ParameterNotFoundException {
        Map<String, Map> signInMap = AdmService.SIGN_IN_MAP;
        List<ScheduleWithHistorySignIn> scheduleWithHistorySignInList;
        try {
            scheduleWithHistorySignInList = courseMapper.getHistorySignIn(
                    transSchedule.getYear(currentDateTime),
                    transSchedule.getTerm(currentDateTime),
                    course.getCozId(),
                    student.getUsrId()
            );
        } catch (NullPointerException e) {
            throw new ParameterNotFoundException("Parameter not found! Expect course.cozId, student.usrId");
        }

        //格式换换成JSON
        JSONArray jSchWsiList = new JSONArray(scheduleWithHistorySignInList);

        jSchWsiList.forEach((jSchWsiObj) -> {
            JSONObject jSchWsi = (JSONObject) jSchWsiObj;

            //数据库查出的两个列表
            JSONArray signInList = jSchWsi.getJSONArray("signInList");
            JSONArray manAbsRecList = jSchWsi.getJSONArray("manAbsRecList");

            //签到列表
            signInList.forEach((signInObj) -> {
                JSONObject signIn = (JSONObject) signInObj;
                JSONArray signInRecList = signIn.getJSONArray("signInRecList");
                signIn.remove("signInRecList");
                //无记录
                if (signInRecList.length() <= 0) {
                    AdmService.addMsgForNotSignIn(
                            currentDateTime,
                            jSchWsi.getInt("schStartTime"),
                            jSchWsi.getInt("schDay"),
                            signIn.getInt("siWeek"),
                            signIn.getBoolean("siAuto"),
                            LocalDateTime.parse(signIn.getString("siTime").replace(" ", "T")),
                            signIn, transSchedule);
                    return;
                }
                Integer signInIndex = null;
                //签到记录有可能因不同原因产生数条记录
                for (int k = 0; k < signInRecList.length(); k++) {
                    JSONObject signInRec = signInRecList.getJSONObject(k);
                    String key = "1_" + (signInRec.getBoolean("sirLeave") ? 1 : 0) + "_" + signInRec.getInt("sirApprove");
                    signIn.put("signInStatusMsg", signInMap.get(key).get("msg"));
                    signIn.put("signInStatusCode", signInMap.get(key).get("status"));
                    //记录请假批准记录
                    if (signInIndex == null && key.equals("1_1_1")) {
                        signInIndex = k;
                    }
                    //记录签到记录
                    if (key.equals("1_0_1") || key.equals("1_0_0")) {
                        signInIndex = k;
                        break;
                    }
                }

                //更新signInRec
                if (signInIndex != null) {
                    signIn.put("signInRec", signInRecList.get(signInIndex));
                    return;
                }
                signIn.put("signInRec", signInRecList.get(0));
            });

            //老师记录列表
            manAbsRecList.forEach((manAbsRecObj) -> {
                JSONObject manAbsRec = (JSONObject) manAbsRecObj;
                JSONArray marDtlList = manAbsRec.getJSONArray("marDtlList");
                JSONObject marDtl = marDtlList.length() == 0 ? null : marDtlList.getJSONObject(0);//移出数组
                manAbsRec.put("marDtl", marDtl);
                manAbsRec.remove("marDtlList");
            });
        });
        return jSchWsiList;
    }


    /*=====================================================================================================*/
    //judge util                                                                                           //
    /*=====================================================================================================*/
    private boolean isManualTimeRes(SignIn standardSignIn, LocalTime tempLocalTime) {
        LocalDateTime deadDateTime = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0, 0);
        return (!standardSignIn.getSiTime().isEqual(deadDateTime)) && judgeTime(standardSignIn.getSiTime().toLocalTime(), tempLocalTime);
    }

    private boolean isDefaultTimeRes(LocalTime stdLocalTime, LocalTime tempLocalTime, SignIn standardSignIn) {
        return standardSignIn.getSiAuto() && judgeTime(stdLocalTime, tempLocalTime);
    }

    private boolean judgeLocation(double lat1, double lng1, double lat2, double lng2) {
        double distance = LocationUtil.getDistance(lat1, lng1, lat2, lng2);
        return (distance <= MAX_DISTANCE);
    }

    private boolean judgeTime(LocalTime stdLocalTime, LocalTime tempLocalTime) {
        long minutes = stdLocalTime.until(tempLocalTime, ChronoUnit.MINUTES);
        return minutes <= 10 && minutes >= 0;
    }
}


