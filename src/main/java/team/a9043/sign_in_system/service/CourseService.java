package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.async.AsyncJoinService;
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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private AsyncJoinService asyncJoinService;

    public CourseService() {
    }

    @SuppressWarnings("Duplicates")
    public JSONObject getCourses(@Nullable Boolean needMonitor,
                                 @Nullable Boolean hasMonitor,
                                 @Nullable Integer page) throws IncorrectParameterException, ExecutionException, InterruptedException {
        if (null == page) {
            throw new IncorrectParameterException("Incorrect page: " + page);
        }
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");

        PageHelper.startPage(page, coursePageSize);
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

        List<String> suIdList = new ArrayList<>();
        List<String> scIdList = new ArrayList<>();
        pageInfo.getList()
            .forEach(sisCourse -> {
                suIdList.add(sisCourse.getSuId());
                scIdList.add(sisCourse.getScId());
            });

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
            sisJoinCourseList.parallelStream().map(SisJoinCourse::getSuId).collect(Collectors.toList());
        Future<List<team.a9043.sign_in_system.pojo.SisUser>> joinCoursesSisUserListFuture =
            asyncJoinService.joinSisUserById(joinCoursesSuIdList);

        List<SisSupervision> sisSupervisionList =
            sisSupervisionListFuture.get();
        List<team.a9043.sign_in_system.pojo.SisUser> joinCoursesSisUserList
            = joinCoursesSisUserListFuture.get();

        //merge to json
        JSONObject pageJson = new JSONObject(pageInfo);
        pageJson.getJSONArray("list")
            .forEach(sisCourseObj -> {
                JSONObject sisCourseJson = (JSONObject) sisCourseObj;
                //merge monitor
                sisCourseJson.put("monitor", sisUserList.stream()
                    .filter(sisUser -> sisUser.getSuId().equals(sisCourseJson.getString("suId")))
                    .findAny()
                    .map(sisUser -> {
                        sisUser.setSuPassword(null);
                        return sisUser;
                    })
                    .map(JSONObject::new)
                    .orElse(null));

                //merge schedule
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

                //merge joinCourse
                JSONArray sisJoinCourseJsonArray = new JSONArray(
                    sisJoinCourseList.parallelStream()
                        .filter(sisJoinCourse -> sisJoinCourse.getScId().equals(sisCourseJson.getString("scId")))
                        .collect(Collectors.toList()));
                mergeSisJoinCourseEtSisUser(joinCoursesSisUserList,
                    sisJoinCourseJsonArray);

                sisCourseJson.put("sisJoinCourseList", sisJoinCourseJsonArray);
            });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("data", pageJson);
        return jsonObject;
    }

    public JSONObject getCourses(@TokenUser SisUser sisUser) throws ExecutionException, InterruptedException {
        SisJoinCourseExample sisJoinCourseExample = new SisJoinCourseExample();
        sisJoinCourseExample.createCriteria().andSuIdEqualTo(sisUser.getSuId());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        if (sisJoinCourseList.size() <= 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "No courses");
            return jsonObject;
        }

        List<String> scIdList = sisJoinCourseList.stream()
            .map(SisJoinCourse::getScId)
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
        List<SisJoinCourse> sisJoinCourseList1 = sisJoinCourseListFuture.get();

        List<String> suIdList = sisJoinCourseList1.parallelStream()
            .map(SisJoinCourse::getSuId)
            .collect(Collectors.toList());
        SisUserExample sisUserExample = new SisUserExample();
        sisUserExample.createCriteria().andSuIdIn(suIdList);
        List<team.a9043.sign_in_system.pojo.SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);

        //merge sisJoinCourse
        JSONArray jsonArray = new JSONArray(sisJoinCourseList);
        jsonArray.forEach(sisJoinCourseObj -> {
            JSONObject sisJoinCourseJson = (JSONObject) sisJoinCourseObj;

            String scId = sisJoinCourseJson.getString("scId");
            SisCourse sisCourse =
                sisCourseList.stream()
                    .filter(tSisCourse -> tSisCourse.getScId().equals(scId))
                    .findAny()
                    .orElse(null);
            if (null == sisCourse)
                return;

            //merge course
            JSONObject sisCourseJson = new JSONObject(sisCourse);

            //merge course -> schedule
            List<SisSchedule> tSisScheduleList =
                sisScheduleList.parallelStream()
                    .filter(sisSchedule -> sisSchedule.getScId().equals(scId))
                    .collect(Collectors.toList());
            sisCourseJson.put("sisScheduleList",
                new JSONArray(tSisScheduleList));

            //merge course -> joinCourse
            List<SisJoinCourse> tSisJoinCourseList =
                sisJoinCourseList1.parallelStream()
                    .filter(sisJoinCourse -> sisJoinCourse.getScId().equals(scId))
                    .collect(Collectors.toList());
            JSONArray sisJoinCourseJsonArray =
                new JSONArray(tSisJoinCourseList);
            mergeSisJoinCourseEtSisUser(sisUserList, sisJoinCourseJsonArray);
            sisCourseJson.put("sisJoinCourseList", sisJoinCourseJsonArray);

            sisJoinCourseJson.put("sisCourse", sisCourseJson);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("array", jsonArray);
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


    //----------------------------util------------------------------//

    private void mergeSisJoinCourseEtSisUser(List<team.a9043.sign_in_system.pojo.SisUser> sisUserList,
                                             JSONArray sisJoinCourseJsonArray) {
        sisJoinCourseJsonArray.forEach(sisJoinCourseObj2 -> {
            JSONObject sisJoinCourseJson2 = (JSONObject) sisJoinCourseObj2;
            team.a9043.sign_in_system.pojo.SisUser tSisUser =
                sisUserList.parallelStream()
                    .filter(tSisUser1 -> tSisUser1.getSuId().equals(sisJoinCourseJson2.getString("suId")))
                    .findAny()
                    .map(tSisUser1 -> {
                        tSisUser1.setSuPassword(null);
                        return tSisUser1;
                    })
                    .orElse(null);
            sisJoinCourseJson2.put("sisUser", null == tSisUser ? null :
                new JSONObject(tSisUser));
        });
    }
}
