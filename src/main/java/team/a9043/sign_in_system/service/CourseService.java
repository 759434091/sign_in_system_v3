package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system.async.AsyncJoinService;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service_pojo.Week;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
@Slf4j
public class CourseService {
    @Value("${pageSize.coursePageSize}")
    private Integer coursePageSize;
    @Resource
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private SisDepartmentMapper sisDepartmentMapper;
    @Resource
    private AsyncJoinService asyncJoinService;
    @Resource
    private SisJoinDepartMapper sisJoinDepartMapper;

    public Week getWeek(LocalDateTime currentDateTime) throws InvalidTimeParameterException {
        int week = JudgeTimeUtil.getWeek(currentDateTime.toLocalDate());
        return new Week(currentDateTime, week);
    }

    public List<SisDepartment> getDepartments(String sdName) {

        SisDepartmentExample sisDepartmentExample = new SisDepartmentExample();
        sisDepartmentExample.createCriteria().andSdNameLike(getFuzzySearch(sdName));

        return sisDepartmentMapper.selectByExample(sisDepartmentExample);
    }

    @SuppressWarnings("Duplicates")
    public PageInfo<SisCourse> getCourses(@Nullable Integer page,
                                          @Nullable Integer pageSize,
                                          @Nullable Boolean needMonitor,
                                          @Nullable Boolean hasMonitor,
                                          @Nullable Integer sdId,
                                          @Nullable Integer scGrade,
                                          @Nullable String scId,
                                          @Nullable String scName) throws IncorrectParameterException, ExecutionException, InterruptedException {
        if (null == page) {
            throw new IncorrectParameterException("Incorrect page: " + null);
        }
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (null == pageSize)
            pageSize = coursePageSize;
        else if (pageSize <= 0 || pageSize > 500) {
            throw new IncorrectParameterException("pageSize must between [1," +
                "500]");
        }

        SisCourseExample sisCourseExample = new SisCourseExample();
        SisCourseExample.Criteria criteria = sisCourseExample.createCriteria();
        if (null != needMonitor) {
            if (null == hasMonitor)
                criteria.andScNeedMonitorEqualTo(needMonitor);
            else if (hasMonitor)
                criteria.andScNeedMonitorEqualTo(needMonitor).andSuIdIsNotNull();
            else
                criteria.andScNeedMonitorEqualTo(needMonitor).andSuIdIsNull();
        }
        if (null != scGrade) criteria.andScGradeEqualTo(scGrade);
        if (null != scName) criteria.andScNameLike(getFuzzySearch(scName));
        if (null != scId) criteria.andScIdLike("%" + scId + "%");
        if (null != sdId) sisCourseExample.setSdId(sdId);

        PageHelper.startPage(page, pageSize);
        List<SisCourse> sisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);
        PageInfo<SisCourse> pageInfo = new PageInfo<>(sisCourseList);

        if (sisCourseList.isEmpty()) return pageInfo;

        Set<String> suIdSet = new HashSet<>();
        Set<String> scIdSet = new HashSet<>();
        pageInfo.getList()
            .forEach(sisCourse -> {
                suIdSet.add(sisCourse.getSuId());
                scIdSet.add(sisCourse.getScId());
            });
        List<String> suIdList = new ArrayList<>(suIdSet);
        List<String> scIdList = new ArrayList<>(scIdSet);

        //join monitor & schedules & joinCourses
        Future<List<team.a9043.sign_in_system.pojo.SisUser>> monitorListFuture =
            asyncJoinService.joinSisUserById(suIdList);
        Future<List<SisSchedule>> sisScheduleListFuture =
            asyncJoinService.joinSisScheduleByForeignKey(scIdList);
        Future<List<SisJoinCourse>> sisJoinCourseListFuture =
            asyncJoinService.joinSisJoinCourseByForeignKey(scIdList);

        List<team.a9043.sign_in_system.pojo.SisUser> sisUserList =
            monitorListFuture.get();
        List<SisSchedule> sisScheduleList =
            sisScheduleListFuture.get();
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseListFuture.get();

        //join schedule -> supervision
        //join joinCourse -> user
        List<Integer> ssIdList = sisScheduleList.stream()
            .map(SisSchedule::getSsId)
            .collect(Collectors.toList());
        Future<List<SisSupervision>> sisSupervisionListFuture =
            asyncJoinService.joinSisSupervisionByForeignKey(ssIdList);
        List<String> joinCoursesSuIdList =
            sisJoinCourseList.parallelStream()
                .map(SisJoinCourse::getSuId)
                .distinct()
                .collect(Collectors.toList());
        Future<List<SisUser>> joinCoursesSisUserListFuture =
            asyncJoinService.joinSisUserById(joinCoursesSuIdList);

        List<SisSupervision> sisSupervisionList =
            sisSupervisionListFuture.get();
        List<SisUser> joinCoursesSisUserList
            = joinCoursesSisUserListFuture.get();

        //merge
        pageInfo.getList().parallelStream()
            .forEach(c -> {
                // set monitor
                c.setMonitor(sisUserList.stream()
                    .filter(sisUser -> sisUser.getSuId().equals(c.getSuId()))
                    .findAny()
                    .map(sisUser -> {
                        sisUser.setSuPassword(null);
                        return sisUser;
                    })
                    .orElse(null));

                // set schedule
                List<SisSchedule> tSchList = sisScheduleList.stream()
                    .filter(sisSchedule -> sisSchedule.getScId().equals(c.getScId()))
                    .collect(Collectors.toList());

                tSchList.forEach(s -> s.setSisSupervisionList(
                    sisSupervisionList.stream()
                        .filter(sisSupervision -> sisSupervision.getSsId().equals(s.getSsId()))
                        .collect(Collectors.toList())));

                c.setSisScheduleList(sisScheduleList);

                // set joinCourse
                List<SisJoinCourse> tJoinCourseList = sisJoinCourseList
                    .parallelStream()
                    .filter(sisJoinCourse -> sisJoinCourse.getScId().equals(c.getScId()))
                    .collect(Collectors.toList());

                tJoinCourseList.forEach(j -> j.setSisUser(joinCoursesSisUserList.parallelStream()
                    .filter(u -> u.getSuId().equals(j.getSuId()))
                    .peek(u -> u.setSuPassword(null))
                    .findAny()
                    .orElse(null)));

                c.setSisJoinCourseList(tJoinCourseList);
            });

        if (log.isDebugEnabled()) {
            log.debug("select course: CourseService.getCourses(..)");
        }
        return pageInfo;
    }

    public PageInfo<SisJoinCourse> getCourses(@TokenUser SisUser sisUser,
                                              SisJoinCourse.JoinCourseType joinCourseType) throws ExecutionException, InterruptedException {
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andSuIdEqualTo(sisUser.getSuId())
            .andJoinCourseTypeEqualTo(joinCourseType.ordinal());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
        PageInfo<SisJoinCourse> sisJoinCoursePageInfo =
            new PageInfo<>(sisJoinCourseList);

        if (sisJoinCourseList.isEmpty()) return sisJoinCoursePageInfo;

        List<String> scIdList = sisJoinCourseList.stream()
            .map(SisJoinCourse::getScId)
            .distinct()
            .collect(Collectors.toList());

        //join course schedule joinCourse
        Future<List<SisCourse>> sisCourseListFuture =
            asyncJoinService.joinSisCourseById(scIdList);
        Future<List<SisSchedule>> sisScheduleListFuture =
            asyncJoinService.joinSisScheduleByForeignKey(scIdList);
        Future<List<SisJoinCourse>> sisJoinCourseListFuture =
            asyncJoinService.joinSisJoinCourseByForeignKey(scIdList);

        List<SisCourse> sisCourseList = sisCourseListFuture.get();
        List<SisSchedule> sisScheduleList = sisScheduleListFuture.get();
        List<SisJoinCourse> totalJoinCourseList = sisJoinCourseListFuture.get();

        List<String> suIdList = Optional.of(totalJoinCourseList.parallelStream()
            .map(SisJoinCourse::getSuId)
            .distinct()
            .collect(Collectors.toList()))
            .filter(l -> !l.isEmpty())
            .orElse(new ArrayList<>());
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuIdIn(suIdList);
        List<SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);

        //merge sisJoinCourse
        sisJoinCoursePageInfo.getList().forEach(j -> {
            // get course
            SisCourse sisCourse =
                sisCourseList.stream()
                    .filter(c -> c.getScId().equals(j.getScId()))
                    .findAny()
                    .orElse(null);
            if (null == sisCourse) return;

            // merge course -> schedule
            sisCourse.setSisScheduleList(sisScheduleList.parallelStream()
                .filter(sisSchedule -> sisSchedule.getScId().equals(j.getScId()))
                .collect(Collectors.toList()));

            // merge course -> joinCourse
            List<SisJoinCourse> tJoinCourseList =
                totalJoinCourseList.parallelStream()
                    .filter(sisJoinCourse -> sisJoinCourse.getScId().equals(j.getScId()))
                    .collect(Collectors.toList());
            tJoinCourseList.forEach(tj -> tj.setSisUser(sisUserList.parallelStream()
                .filter(u -> tj.getSuId().equals(u.getSuId()))
                .peek(u -> u.setSuPassword(null))
                .findAny()
                .orElse(null)));
            sisCourse.setSisJoinCourseList(tJoinCourseList);

            // merge all
            j.setSisCourse(sisCourse);
        });

        return sisJoinCoursePageInfo;
    }

    @Transactional
    public JSONObject modifyScNeedMonitor(SisCourse sisCourse) throws IncorrectParameterException {
        SisCourse stdSisCourse =
            sisCourseMapper.selectByPrimaryKey(sisCourse.getScId());
        if (null == stdSisCourse)
            throw new IncorrectParameterException(
                "No course: " + sisCourse.getScId());

        boolean scNeedMonitor = sisCourse.getScNeedMonitor();
        stdSisCourse.setScNeedMonitor(scNeedMonitor);
        if (!scNeedMonitor)
            stdSisCourse.setSuId(null);

        JSONObject sisCourseJson = new JSONObject(stdSisCourse);
        if (null != stdSisCourse.getSuId()) {
            SisUser sisUser =
                sisUserMapper.selectByPrimaryKey(stdSisCourse.getSuId());
            if (null != sisUser)
                sisCourseJson.put("sisUser", new JSONObject(sisUser));
        }

        boolean success = sisCourseMapper.updateByPrimaryKey(stdSisCourse) > 0;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        if (success) {
            jsonObject.put("sisCourse", sisCourseJson);
            log.info("Success modify scNeedMonitor: scId " + sisCourse.getScId());
        }
        return jsonObject;
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public JSONObject batchSupervision(@NonNull boolean monitorStatus,
                                       @Nullable Boolean needMonitor,
                                       @Nullable Boolean hasMonitor,
                                       @Nullable Integer sdId,
                                       @Nullable Integer scGrade,
                                       @Nullable String scId,
                                       @Nullable String scName) {
        SisCourseExample sisCourseExample = new SisCourseExample();
        SisCourseExample.Criteria criteria = sisCourseExample.createCriteria();
        if (null != needMonitor) {
            if (null == hasMonitor)
                criteria.andScNeedMonitorEqualTo(needMonitor);
            else if (hasMonitor)
                criteria.andScNeedMonitorEqualTo(needMonitor).andSuIdIsNotNull();
            else
                criteria.andScNeedMonitorEqualTo(needMonitor).andSuIdIsNull();
        }
        if (null != scGrade)
            criteria.andScGradeEqualTo(scGrade);
        if (null != scName)
            criteria.andScNameLike(getFuzzySearch(scName));
        if (null != scId) {
            criteria.andScIdLike("%" + scId + "%");
        }
        if (null != sdId) {
            sisCourseExample.setSdId(sdId);
        }

        List<SisCourse> stdSisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);
        stdSisCourseList.parallelStream()
            .forEach(sisCourse -> {
                sisCourse.setScNeedMonitor(monitorStatus);
                if (!monitorStatus) {
                    sisCourse.setSuId(null);
                }
            });

        JSONObject jsonObject = new JSONObject();
        boolean success =
            sisCourseMapper.updateNeedMonitorList(stdSisCourseList) > 0;
        jsonObject.put("success", success);
        if (success)
            log.info("Success batchSupervision: monitorStatus " + monitorStatus);
        return jsonObject;
    }

    @Transactional
    public JSONObject batchSupervision(@NonNull boolean monitorStatus,
                                       @NonNull List<String> scIdList) {
        SisCourseExample sisCourseExample = new SisCourseExample();
        sisCourseExample.createCriteria().andScIdIn(scIdList);
        List<SisCourse> stdSisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);
        stdSisCourseList.parallelStream()
            .forEach(sisCourse -> {
                sisCourse.setScNeedMonitor(monitorStatus);
                if (!monitorStatus) {
                    sisCourse.setSuId(null);
                }
            });

        JSONObject jsonObject = new JSONObject();
        boolean success =
            sisCourseMapper.updateNeedMonitorList(stdSisCourseList) > 0;
        jsonObject.put("success", success);
        if (success)
            log.info("Success batchSupervision: monitorStatus " + monitorStatus + " scIdList -> " + new JSONArray(scIdList));
        return jsonObject;
    }


    @SuppressWarnings("Duplicates")
    public JSONObject getCourseDepartments(String scId) {
        SisJoinDepartExample sisJoinDepartExample = new SisJoinDepartExample();
        sisJoinDepartExample.createCriteria().andScIdEqualTo(scId);
        List<SisJoinDepart> sisJoinDepartList =
            sisJoinDepartMapper.selectByExample(sisJoinDepartExample);
        if (sisJoinDepartList.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("array", new ArrayList<>());
            return jsonObject;
        }

        SisDepartmentExample sisDepartmentExample = new SisDepartmentExample();
        sisDepartmentExample.createCriteria().andSdIdIn(sisJoinDepartList.stream().map(SisJoinDepart::getSdId).distinct().collect(Collectors.toList()));
        List<SisDepartment> sisDepartmentList =
            sisDepartmentMapper.selectByExample(sisDepartmentExample);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("array", new JSONArray(sisDepartmentList));
        return jsonObject;
    }


    @SuppressWarnings("Duplicates")
    public JSONObject getJoinCourseStudents(String scId) {
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria()
            .andScIdEqualTo(scId)
            .andJoinCourseTypeEqualTo(SisJoinCourse.JoinCourseType.ATTENDANCE.ordinal());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
        if (sisJoinCourseList.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("array", new ArrayList<>());
            return jsonObject;
        }

        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria()
            .andSuIdIn(sisJoinCourseList.stream().map(SisJoinCourse::getSuId).distinct().collect(Collectors.toList()));

        List<SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);
        JSONArray joinCourseJsonArray = new JSONArray(sisJoinCourseList);
        joinCourseJsonArray.forEach(jcObj -> {
            JSONObject jcJson = (JSONObject) jcObj;
            String suId = jcJson.getString("suId");
            SisUser sisUser1 =
                sisUserList.stream().filter(s -> s.getSuId().equals(suId)).findAny().orElse(null);
            jcJson.put("sisUser", null == sisUser1 ? null :
                new JSONObject(sisUser1));
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("array", joinCourseJsonArray);
        return jsonObject;
    }


    //----------------------------util------------------------------//

    static void mergeWithSuIdJsonArrayEtSisUser(List<SisUser> sisUserList,
                                                JSONArray withSuIdJsonArray) {
        withSuIdJsonArray.forEach(withSuIdObj -> {
            JSONObject withSuIdJson = (JSONObject) withSuIdObj;

            String suId = withSuIdJson.optString("suId", null);
            SisUser tSisUser =
                sisUserList.parallelStream()
                    .filter(tSisUser1 -> tSisUser1.getSuId().equals(suId))
                    .findAny()
                    .map(tSisUser1 -> {
                        tSisUser1.setSuPassword(null);
                        return tSisUser1;
                    })
                    .orElse(null);

            withSuIdJson.put("sisUser", null == tSisUser ? null :
                new JSONObject(tSisUser));
        });
    }

    static List<SisUser> getSisUserBySuIdList(List<String> suIdList,
                                              SisUserMapper sisUserMapper) {
        if (suIdList.isEmpty()) {
            return new ArrayList<>();
        } else {
            SisUserExample sisUserExample = new SisUserExample();
            sisUserExample.createCriteria().andSuIdIn(suIdList);
            return sisUserMapper.selectByExample(sisUserExample);
        }
    }

    public static String getFuzzySearch(String fuzzyName) {
        return Optional
            .ofNullable(fuzzyName)
            .filter(name -> !name.equals(""))
            .map((name) -> {
                StringBuilder cozSearchBuilder = new StringBuilder();
                Arrays.stream(name.split("")).forEach(ch -> cozSearchBuilder.append("%").append(ch));
                cozSearchBuilder.append("%");
                return cozSearchBuilder.toString();
            })
            .orElse("%");
    }
}
