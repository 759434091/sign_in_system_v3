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
import team.a9043.sign_in_system.service_pojo.VoidOperationResponse;
import team.a9043.sign_in_system.service_pojo.VoidSuccessOperationResponse;
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
    @Resource
    private OtherMapper otherMapper;

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
        if (null == page)
            throw new IncorrectParameterException("Incorrect page: " + null);
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (null == pageSize)
            pageSize = coursePageSize;
        else if (pageSize <= 0 || pageSize > 500)
            throw new IncorrectParameterException("pageSize must between [1," +
                "500]");

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

        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria()
            .andJoinCourseTypeEqualTo(SisJoinCourse.JoinCourseType.TEACHING.ordinal())
            .andScIdIn(scIdList);

        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuIdIn(sisJoinCourseList.parallelStream()
            .map(SisJoinCourse::getSuId)
            .distinct()
            .collect(Collectors.toList()));
        List<SisUser> teacherList =
            sisUserMapper.selectByExample(sisUserExample);
        sisJoinCourseList.forEach(j -> j.setSisUser(teacherList.parallelStream()
            .filter(u -> u.getSuId().equals(j.getSuId()))
            .peek(u -> u.setSuPassword(null))
            .findAny()
            .orElse(null)));

        //join monitor & schedules
        Future<List<SisUser>> monitorListFuture =
            asyncJoinService.joinSisUserById(suIdList);
        Future<List<SisSchedule>> sisScheduleListFuture =
            asyncJoinService.joinSisScheduleByForeignKey(scIdList);

        List<team.a9043.sign_in_system.pojo.SisUser> sisUserList =
            monitorListFuture.get();
        List<SisSchedule> sisScheduleList =
            sisScheduleListFuture.get();

        //join schedule -> supervision
        List<Integer> ssIdList = sisScheduleList.parallelStream()
            .map(SisSchedule::getSsId)
            .collect(Collectors.toList());
        Future<List<SisSupervision>> sisSupervisionListFuture =
            asyncJoinService.joinSisSupervisionByForeignKey(ssIdList);
        List<SisSupervision> sisSupervisionList =
            sisSupervisionListFuture.get();

        //merge
        pageInfo.getList().parallelStream()
            .forEach(c -> {
                // set monitor
                c.setMonitor(sisUserList.parallelStream()
                    .filter(sisUser -> sisUser.getSuId().equals(c.getSuId()))
                    .findAny()
                    .map(sisUser -> {
                        sisUser.setSuPassword(null);
                        return sisUser;
                    })
                    .orElse(null));

                c.setSisJoinCourseList(sisJoinCourseList.parallelStream()
                    .filter(j -> j.getScId().equals(c.getScId()))
                    .collect(Collectors.toList()));

                // set schedule
                List<SisSchedule> tSchList = sisScheduleList.parallelStream()
                    .filter(sisSchedule -> sisSchedule.getScId().equals(c.getScId()))
                    .collect(Collectors.toList());

                tSchList.forEach(s -> s.setSisSupervisionList(
                    sisSupervisionList.stream()
                        .filter(sisSupervision -> sisSupervision.getSsId().equals(s.getSsId()))
                        .collect(Collectors.toList())));

                c.setSisScheduleList(tSchList);
            });

        if (log.isDebugEnabled()) {
            log.debug("select course: CourseService.getCourses(..)");
        }
        return pageInfo;
    }

    public PageInfo<SisCourse> getStudentCourses(@TokenUser SisUser sisUser) {
        List<SisCourse> sisCourseList =
            otherMapper.selectStuCozTable(sisUser.getSuId(), true);
        return new PageInfo<>(sisCourseList);
    }

    public PageInfo<SisCourse> getTeacherCourses(@TokenUser SisUser sisUser) {
        List<SisCourse> sisCourseList =
            otherMapper.selectStuCozTable(sisUser.getSuId(), false);
        return new PageInfo<>(sisCourseList);
    }

    @Transactional
    public VoidOperationResponse modifyScNeedMonitor(SisCourse sisCourse) throws IncorrectParameterException {
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
        sisCourseMapper.updateByPrimaryKey(stdSisCourse);
        log.info("Success modify scNeedMonitor: scId " + sisCourse.getScId());
        return VoidSuccessOperationResponse.SUCCESS;
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public VoidOperationResponse batchSetNeedMonitor(@NonNull boolean monitorStatus,
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
        if (null != scGrade) criteria.andScGradeEqualTo(scGrade);
        if (null != scName) criteria.andScNameLike(getFuzzySearch(scName));
        if (null != scId) criteria.andScIdLike("%" + scId + "%");
        if (null != sdId) sisCourseExample.setSdId(sdId);

        List<SisCourse> stdSisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);
        stdSisCourseList.parallelStream()
            .forEach(sisCourse -> {
                sisCourse.setScNeedMonitor(monitorStatus);
                if (!monitorStatus) sisCourse.setSuId(null);
            });

        sisCourseMapper.updateNeedMonitorList(stdSisCourseList);
        log.info("Success batchSetNeedMonitor: monitorStatus " + monitorStatus);
        return VoidSuccessOperationResponse.SUCCESS;
    }

    @Transactional
    public VoidOperationResponse batchSetNeedMonitor(@NonNull boolean monitorStatus,
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

        sisCourseMapper.updateNeedMonitorList(stdSisCourseList);
        log.info("Success batchSetNeedMonitor: monitorStatus " + monitorStatus + " scIdList -> " + new JSONArray(scIdList));
        return VoidSuccessOperationResponse.SUCCESS;
    }

    public List<SisDepartment> getCourseDepartments(String scId) {
        SisJoinDepartExample sisJoinDepartExample = new SisJoinDepartExample();
        sisJoinDepartExample.createCriteria().andScIdEqualTo(scId);
        List<SisJoinDepart> sisJoinDepartList =
            sisJoinDepartMapper.selectByExample(sisJoinDepartExample);
        if (sisJoinDepartList.isEmpty()) return new ArrayList<>();

        SisDepartmentExample sisDepartmentExample = new SisDepartmentExample();
        sisDepartmentExample.createCriteria().andSdIdIn(sisJoinDepartList.stream()
            .map(SisJoinDepart::getSdId)
            .distinct()
            .collect(Collectors.toList()));

        return sisDepartmentMapper.selectByExample(sisDepartmentExample);
    }

    public List<SisJoinCourse> getJoinCourseStudents(String scId) {
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria()
            .andScIdEqualTo(scId)
            .andJoinCourseTypeEqualTo(SisJoinCourse.JoinCourseType.ATTENDANCE.ordinal());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
        if (sisJoinCourseList.isEmpty()) return sisJoinCourseList;

        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria()
            .andSuIdIn(sisJoinCourseList.parallelStream().map(SisJoinCourse::getSuId).distinct().collect(Collectors.toList()));

        List<SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);
        sisJoinCourseList.forEach(j -> j.setSisUser(sisUserList.parallelStream()
            .filter(u -> u.getSuId().equals(j.getSuId()))
            .peek(u -> u.setSuPassword(null))
            .findAny()
            .orElse(null)));

        return sisJoinCourseList;
    }

    //----------------------------util------------------------------//

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

    static String getFuzzySearch(String fuzzyName) {
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
