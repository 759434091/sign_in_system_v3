package team.a9043.sign_in_system.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.repository.SisCourseRepository;
import team.a9043.sign_in_system.repository.SisJoinCourseRepository;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@Service
public class CourseService {
    @Value("${pageSize.coursePageSize}")
    private Integer coursePageSize;
    @Resource
    private SisCourseRepository sisCourseRepository;
    @Resource
    private SisJoinCourseRepository sisJoinCourseRepository;
    @Resource
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisSupervisionMapper sisSupervisionMapper;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("Duplicates")
    public JSONObject getCourses(@Nullable Boolean needMonitor,
                                 @Nullable Boolean hasMonitor,
                                 @NonNull Integer page) throws IncorrectParameterException {
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");

        PageHelper.startPage(page, coursePageSize);
        SisCourseExample sisCourseExample = new SisCourseExample();
        SisCourseExample.Criteria criteria = sisCourseExample.createCriteria();
        if (null != needMonitor) {
            if (null == hasMonitor)
                criteria.andScNeedMonitorEqualTo(true);
            else if (hasMonitor)
                criteria.andScNeedMonitorEqualTo(true).andSuIdIsNotNull();
            else
                criteria.andScNeedMonitorEqualTo(true).andSuIdIsNull();
        } else {
            criteria.andScNeedMonitorEqualTo(false);
        }

        List<SisCourse> sisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);
        PageInfo<SisCourse> pageInfo = new PageInfo<>(sisCourseList);

        if (sisCourseList.size() <= 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("page", page);
            jsonObject.put("message", "No courses");
            return jsonObject;
        }

        //join monitor
        List<String> suIdList = pageInfo.getList()
            .stream()
            .map(SisCourse::getSuId)
            .collect(Collectors.toList());

        List<team.a9043.sign_in_system.pojo.SisUser> sisUserList;
        if (suIdList.isEmpty()) {
            sisUserList = new ArrayList<>();
        } else {
            SisUserExample sisUserExample = new SisUserExample();
            sisUserExample.createCriteria().andSuIdIn(suIdList);
            sisUserList = sisUserMapper.selectByExample(sisUserExample);
        }

        //join schedules
        List<String> scIdList = pageInfo.getList()
            .stream()
            .map(SisCourse::getScId)
            .collect(Collectors.toList());

        List<SisSchedule> sisScheduleList;
        if (scIdList.isEmpty()) {
            sisScheduleList = new ArrayList<>();
        } else {
            SisScheduleExample sisScheduleExample = new SisScheduleExample();
            sisScheduleExample.createCriteria().andScIdIn(scIdList);
            sisScheduleList =
                sisScheduleMapper.selectByExample(sisScheduleExample);
        }

        //join schedules -> supervisions
        List<Integer> ssIdList = sisScheduleList.stream()
            .map(SisSchedule::getSsId)
            .collect(Collectors.toList());

        List<SisSupervision> sisSupervisionList;
        if (ssIdList.isEmpty()) {
            sisSupervisionList = new ArrayList<>();
        } else {
            SisSupervisionExample sisSupervisionExample =
                new SisSupervisionExample();
            sisSupervisionExample.createCriteria().andSsIdIn(ssIdList);
            sisSupervisionList =
                sisSupervisionMapper.selectByExample(sisSupervisionExample);
        }

        //join join courses
        List<SisJoinCourse> sisJoinCourseList;
        if (scIdList.isEmpty()) {
            sisJoinCourseList = new ArrayList<>();
        } else {
            SisJoinCourseExample sisJoinCourseExample =
                new SisJoinCourseExample();
            sisJoinCourseExample.createCriteria().andScIdIn(scIdList);
            sisJoinCourseList =
                sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
        }

        //join join courses -> user
        List<String> joinCoursesSuIdList =
            sisJoinCourseList.stream().map(SisJoinCourse::getSuId).collect(Collectors.toList());

        List<team.a9043.sign_in_system.pojo.SisUser> joinCoursesSisUserList;
        if (joinCoursesSuIdList.isEmpty()) {
            joinCoursesSisUserList = new ArrayList<>();
        } else {
            SisUserExample joinCoursesSisUserExample = new SisUserExample();
            joinCoursesSisUserExample.createCriteria().andSuIdIn(joinCoursesSuIdList);
            joinCoursesSisUserList =
                sisUserMapper.selectByExample(joinCoursesSisUserExample);
        }

        //merge to json
        JSONObject pageJson = new JSONObject(pageInfo);
        pageJson.getJSONArray("list")
            .forEach(sisCourseObj -> {
                JSONObject sisCourseJson = (JSONObject) sisCourseObj;
                sisCourseJson.put("monitor", sisUserList.stream()
                    .filter(sisUser -> sisUser.getSuId().equals(sisCourseJson.getString("suId")))
                    .findAny()
                    .map(sisUser -> {
                        sisUser.setSuPassword(null);
                        return sisUser;
                    })
                    .map(JSONObject::new)
                    .orElse(null));

                JSONArray sisScheduleJsonArray =
                    new JSONArray(sisScheduleList.stream()
                        .filter(sisSchedule -> sisSchedule.getScId().equals(sisCourseJson.getString("scId")))
                        .collect(Collectors.toList()));

                sisScheduleJsonArray.forEach(sisScheduleObj -> {
                    JSONObject sisScheduleJson = (JSONObject) sisScheduleObj;
                    List<SisSupervision> tSisSupervisionList =
                        sisSupervisionList.stream()
                            .filter(sisSupervision -> sisSupervision.getSsId().equals(sisScheduleJson.getInt("ssId")))
                            .collect(Collectors.toList());
                    sisScheduleJson.put("supervisionList",
                        new JSONArray(tSisSupervisionList));
                });
                sisCourseJson.put("sisScheduleList", sisScheduleJsonArray);

                JSONArray sisJoinCourseJsonArray = new JSONArray(
                    sisJoinCourseList.stream()
                        .filter(sisJoinCourse -> sisJoinCourse.getScId().equals(sisCourseJson.getString("scId")))
                        .collect(Collectors.toList()));
                sisJoinCourseJsonArray.forEach(sisJoinCourseObj -> {
                    JSONObject sisJoinCourseJson =
                        (JSONObject) sisJoinCourseObj;
                    team.a9043.sign_in_system.pojo.SisUser tSisUser =
                        joinCoursesSisUserList.stream()
                            .filter(sisUser -> sisUser.getSuId().equals(sisJoinCourseJson.getString("suId")))
                            .findAny()
                            .map(sisUser -> {
                                sisUser.setSuPassword(null);
                                return sisUser;
                            })
                            .orElse(null);
                    sisJoinCourseJson.put("sisUser",
                        null == tSisUser ? null : new JSONObject(tSisUser));
                });

                sisCourseJson.put("sisJoinCourseList", sisJoinCourseJsonArray);
            });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("array", pageJson);
        return jsonObject;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions",
        "Duplicates"})
    @Transactional
    public JSONObject getCourses(@TokenUser SisUser sisUser) {
        team.a9043.sign_in_system.entity.SisJoinCourse tempJoinCourse =
            new team.a9043.sign_in_system.entity.SisJoinCourse();
        tempJoinCourse.setSisUser(sisUser);

        Collection<team.a9043.sign_in_system.entity.SisJoinCourse> sisJoinCourses = sisJoinCourseRepository
            .findAll(Example.of(tempJoinCourse));

        JSONObject jsonObject = new JSONObject();
        if (sisJoinCourses.size() <= 0) {
            jsonObject.put("success", false);
            jsonObject.put("message", "No courses");
            return jsonObject;
        }

        sisJoinCourses
            .parallelStream()
            .forEach(sisJoinCourse -> {
                sisJoinCourse.setSisUser(null);
                team.a9043.sign_in_system.entity.SisCourse sisCourse =
                    sisJoinCourse.getSisCourse();
                sisCourse.setMonitor(null);

                sisCourse
                    .getSisSchedules()
                    .forEach(sisSchedule -> {
                        sisSchedule.setSisCourse(null);
                        sisSchedule.setSisSignIns(null);
                        sisSchedule.setSisSupervisions(null);
                    });

                sisCourse.setSisJoinCourseList(sisCourse
                    .getSisJoinCourseList()
                    .stream()
                    .filter(tSisJoinCourse -> tSisJoinCourse.getJoinCourseType().equals(team.a9043.sign_in_system.entity.SisJoinCourse.JoinCourseType.TEACHING))
                    .peek(tSisJoinCourse -> {
                        Optional.ofNullable(tSisJoinCourse.getSisUser())
                            .ifPresent(sisUser1 -> {
                                sisUser1.setSisJoinCourses(null);
                                sisUser1.setSisCourses(null);
                                sisUser1.setSisSignInDetails(null);
                                sisUser1.setSisMonitorTrans(null);
                                sisUser1.setSuPassword(null);
                            });

                        tSisJoinCourse.setSisCourse(null);
                    })
                    .collect(Collectors.toList()));

                Optional.ofNullable(sisCourse.getMonitor()).ifPresent(monitor -> {
                    monitor.setSisMonitorTrans(null);
                    monitor.setSisSignInDetails(null);
                    monitor.setSisCourses(null);
                    monitor.setSisJoinCourses(null);
                    monitor.setSuPassword(null);
                });
            });

        entityManager.clear();
        jsonObject.put("success", true);
        jsonObject.put("array", sisJoinCourses);
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyScNeedMonitor(team.a9043.sign_in_system.entity.SisCourse sisCourse) throws IncorrectParameterException {
        team.a9043.sign_in_system.entity.SisCourse stdSisCourse =
            sisCourseRepository
                .findById(sisCourse.getScId())
                .map(tSisCourse -> {
                    boolean scNeedMonitor = sisCourse.getScNeedMonitor();

                    tSisCourse.setScNeedMonitor(scNeedMonitor);
                    if (!scNeedMonitor) {
                        tSisCourse.setMonitor(null);
                        return tSisCourse;
                    }

                    SisUser sisUser = sisCourse.getMonitor();
                    if (null != sisUser) {
                        tSisCourse.setMonitor(sisUser);
                    }

                    return tSisCourse;
                })
                .orElseThrow(() -> new IncorrectParameterException("No course" +
                    ": " + sisCourse.getScId()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisCourseRepository.save(stdSisCourse));
        return jsonObject;
    }
}
