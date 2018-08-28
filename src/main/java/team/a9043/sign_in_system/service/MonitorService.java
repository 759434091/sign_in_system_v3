package team.a9043.sign_in_system.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.async.AsyncJoinService;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;
import team.a9043.sign_in_system.util.judgetime.ScheduleParserException;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author a9043
 */
@Service
public class MonitorService {
    @Resource
    private SisCourseMapper sisCourseMapper;
    @Resource
    private SisUserMapper sisUserMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private SisSupervisionMapper sisSupervisionMapper;
    @Resource
    private SisMonitorTransMapper sisMonitorTransMapper;
    @Resource
    private AsyncJoinService asyncJoinService;

    public JSONObject getCourses(@NotNull SisUser sisUser) throws ExecutionException, InterruptedException {
        SisCourseExample sisCourseExample = new SisCourseExample();
        sisCourseExample.createCriteria().andSuIdEqualTo(sisUser.getSuId());

        List<SisCourse> sisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);

        List<String> scIdList = sisCourseList.stream()
            .map(SisCourse::getScId)
            .collect(Collectors.toList());

        //join sisJoinCourse & sisSchedule
        Future<List<SisJoinCourse>> sisJoinCourseListFuture =
            asyncJoinService.joinSisJoinCourseByForeignKey(scIdList);
        Future<List<SisSchedule>> sisScheduleListFuture =
            asyncJoinService.joinSisScheduleByForeignKey(scIdList);

        List<SisJoinCourse> sisJoinCourseList = sisJoinCourseListFuture.get();
        List<SisSchedule> sisScheduleList = sisScheduleListFuture.get();

        List<String> suIdList = sisJoinCourseList.parallelStream()
            .map(SisJoinCourse::getSuId)
            .collect(Collectors.toList());

        // join sisUser
        List<SisUser> sisUserList =
            CourseService.getSisUserBySuIdList(suIdList, sisUserMapper);

        //merge joinCourse
        JSONArray sisJoinCourseJsonArray = new JSONArray(sisJoinCourseList);
        CourseService.mergeWithSuIdJsonArrayEtSisUser(sisUserList,
            sisJoinCourseJsonArray);

        //merge sisCourse
        JSONArray sisCourseJsonArray = new JSONArray(sisCourseList);
        sisCourseJsonArray.forEach(sisCourseObj -> {
            JSONObject sisCourseJson = (JSONObject) sisCourseObj;

            String scId = sisCourseJson.getString("scId");
            List<SisSchedule> tSisScheduleList = sisScheduleList.stream()
                .filter(sisSchedule -> sisSchedule.getScId().equals(scId))
                .collect(Collectors.toList());
            sisCourseJson.put("sisScheduleList",
                new JSONArray(tSisScheduleList));

            List<JSONObject> sisJoinCourseJsonList =
                StreamSupport.stream(sisJoinCourseJsonArray.spliterator(), true)
                    .map(sisJoinCourseObj -> (JSONObject) sisJoinCourseObj)
                    .filter(sisJoinCourseJson -> sisJoinCourseJson.getString(
                        "scId").equals(scId))
                    .collect(Collectors.toList());
            sisCourseJson.put("sisJoinCourseList",
                sisJoinCourseJsonList);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", sisCourseJsonArray);
        jsonObject.put("arrSize", sisCourseJsonArray.length());
        return jsonObject;
    }

    public JSONObject getSupervisions(@NotNull SisUser sisUser,
                                      @NotNull String scId) throws IncorrectParameterException, InvalidPermissionException {

        SisCourse sisCourse = Optional
            .ofNullable(sisCourseMapper.selectByPrimaryKey(scId))
            .orElseThrow(() -> new IncorrectParameterException("No course: " + scId));

        if (!sisUser.getSuId().equals(sisCourse.getSuId())) {
            throw new InvalidPermissionException(
                "Invalid permission: " + scId);
        }

        SisScheduleExample sisScheduleExample = new SisScheduleExample();
        sisScheduleExample.createCriteria().andScIdEqualTo(scId);
        List<SisSchedule> sisScheduleList =
            sisScheduleMapper.selectByExample(sisScheduleExample);

        List<Integer> ssIdList = sisScheduleList.stream()
            .map(SisSchedule::getSsId)
            .collect(Collectors.toList());

        List<SisSupervision> sisSupervisionList;
        if (ssIdList.isEmpty()) {
            sisSupervisionList = new ArrayList<>();
        } else {
            SisSupervisionExample sisSupervisionExample =
                new SisSupervisionExample();
            sisScheduleExample.createCriteria().andSsIdIn(ssIdList);
            sisSupervisionList =
                sisSupervisionMapper.selectByExample(sisSupervisionExample);
        }

        //merge sisSchedule
        JSONArray sisScheduleJsonArray = new JSONArray(sisScheduleList);
        sisScheduleJsonArray.forEach(sisScheduleObj -> {
            JSONObject sisScheduleJson = (JSONObject) sisScheduleObj;

            Integer ssId = sisScheduleJson.getInt("ssId");
            List<SisSupervision> tSisSupervisionList =
                sisSupervisionList.stream()
                    .filter(sisSupervision -> sisSupervision.getSsId().equals(ssId))
                    .collect(Collectors.toList());

            sisScheduleJson.put("sisSupervisionList",
                new JSONArray(tSisSupervisionList));
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", sisScheduleJsonArray);
        jsonObject.put("arrSize", sisScheduleJsonArray.length());
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyMonitor(@NotNull SisUser sisUser,
                                    @NotNull String scId) throws IncorrectParameterException, InvalidPermissionException {
        SisCourse sisCourse = Optional
            .ofNullable(sisCourseMapper.selectByPrimaryKey(scId))
            .orElseThrow(() -> new IncorrectParameterException("No course: " + scId));

        if (null != sisCourse.getSuId()) {
            throw new InvalidPermissionException("Invalid permission: " + scId);
        }

        SisCourse updatedSisCourse = new SisCourse();
        updatedSisCourse.setScId(sisCourse.getScId());
        updatedSisCourse.setSuId(sisUser.getSuId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisCourseMapper.updateByPrimaryKeySelective(updatedSisCourse) > 0);
        return jsonObject;
    }

    @Transactional
    public JSONObject insertSupervision(@NotNull SisUser sisUser,
                                        @NotNull Integer ssId,
                                        @NotNull SisSupervision sisSupervision,
                                        @NotNull LocalDateTime localDateTime) throws IncorrectParameterException, ScheduleParserException, InvalidPermissionException, InvalidTimeParameterException {
        //check exist
        SisSupervisionKey sisSupervisionKey = new SisSupervisionKey();
        sisSupervisionKey.setSsId(ssId);
        sisSupervisionKey.setSsvWeek(sisSupervision.getSsvWeek());

        if (null != sisSupervisionMapper.selectByPrimaryKey(sisSupervisionKey)) {
            throw new InvalidPermissionException("Supervision exist: " + ssId + ", " + sisSupervision.getSsvWeek());
        }

        //valid sisSchedule
        SisSchedule sisSchedule = Optional
            .ofNullable(sisScheduleMapper.selectByPrimaryKey(ssId))
            .orElseThrow(() -> new IncorrectParameterException("No ssId: " + ssId));

        //valid permission
        SisCourse sisCourse =
            sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());
        if (!sisUser.getSuId().equals(sisCourse.getSuId())) {
            SisMonitorTransKey sisMonitorTransKey = new SisMonitorTransKey();
            sisMonitorTransKey.setSmtWeek(sisSupervision.getSsvWeek());
            sisMonitorTransKey.setSsId(ssId);

            Optional
                .ofNullable(sisMonitorTransMapper.selectByPrimaryKey(sisMonitorTransKey))
                .filter(sisMonitorTrans ->
                    sisMonitorTrans.getSmtStatus().equals(team.a9043.sign_in_system.entity.SisMonitorTrans.SmtStatus.AGREE.ordinal()) &&
                        sisUser.getSuId().equals(sisMonitorTrans.getSuId()))
                .orElseThrow(() ->
                    new InvalidPermissionException("No permission: " + sisSchedule.getSsId()));
        }

        //judge time
        if (!JudgeTimeUtil.isCourseTime(sisSchedule,
            sisSupervision.getSsvWeek(),
            localDateTime)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Incorrect time");
            return jsonObject;
        }

        sisSupervision.setSsId(ssId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisSupervisionMapper.insert(sisSupervision) > 0);
        return jsonObject;
    }

    public JSONObject getTransCourses(@NotNull SisUser sisUser,
                                      Integer smtStatus) {
        SisMonitorTransExample sisMonitorTransExample =
            new SisMonitorTransExample();
        sisMonitorTransExample.createCriteria()
            .andSuIdEqualTo(sisUser.getSuId())
            .andSmtStatusEqualTo(smtStatus);
        List<JSONObject> sisMonitorTransJsonList = sisMonitorTransMapper
            .selectByExample(sisMonitorTransExample)
            .stream()
            .map(sisMonitorTrans -> {
                //join schedule
                SisSchedule sisSchedule =
                    sisScheduleMapper.selectByPrimaryKey(sisMonitorTrans.getSsId());

                //join course
                SisCourse sisCourse =
                    sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());

                //join monitor
                JSONObject sisCourseJson = new JSONObject(sisCourse);
                if (null != sisCourse.getSuId()) {
                    SisUser monitor =
                        sisUserMapper.selectByPrimaryKey(sisCourse.getSuId());
                    sisCourseJson.put("sisUser", new JSONObject(monitor));
                }

                //merge all
                JSONObject sisScheduleJson = new JSONObject(sisSchedule);
                sisScheduleJson.put("sisCourse", sisCourseJson);

                JSONObject sisMonitorTransJson =
                    new JSONObject(sisMonitorTrans);
                sisMonitorTransJson.put("sisSchedule", sisScheduleJson);
                return sisMonitorTransJson;
            })
            .collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("array", sisMonitorTransJsonList);
        jsonObject.put("arrSize", sisMonitorTransJsonList.size());
        return jsonObject;
    }

    @Transactional
    public JSONObject applyForTransfer(@NotNull SisUser sisUser,
                                       @NotNull Integer ssId,
                                       @NotNull SisMonitorTrans sisMonitorTrans) throws InvalidPermissionException, IncorrectParameterException {
        SisSchedule sisSchedule = Optional
            .ofNullable(sisScheduleMapper.selectByPrimaryKey(ssId))
            .orElseThrow(() ->
                new IncorrectParameterException("Incorrect ssId" + ssId));

        SisMonitorTransKey sisMonitorTransKey = new SisMonitorTransKey();
        sisMonitorTransKey.setSsId(ssId);
        sisMonitorTransKey.setSmtWeek(sisMonitorTrans.getSmtWeek());
        SisMonitorTrans stdSisMonitorTrans =
            sisMonitorTransMapper.selectByPrimaryKey(sisMonitorTransKey);
        if (null != stdSisMonitorTrans) {
            throw new InvalidPermissionException("MonitorTrans exist: " + new JSONObject(sisMonitorTransKey).toString());
        }

        SisCourse sisCourse =
            sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());
        if (null == sisCourse || null == sisCourse.getSuId() || !sisCourse.getSuId().equals(sisUser.getSuId())) {
            throw new InvalidPermissionException("Invalid Permission: ssId " + sisSchedule.getSsId());
        }

        sisMonitorTrans.setSmtStatus(team.a9043.sign_in_system.entity.SisMonitorTrans.SmtStatus.UNTREATED.ordinal());
        sisMonitorTrans.setSuId(sisUser.getSuId());
        sisMonitorTrans.setSsId(ssId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisMonitorTransMapper.insert(sisMonitorTrans) > 0);
        return jsonObject;
    }

    @Transactional
    public JSONObject modifyTransfer(@NotNull SisUser sisUser,
                                     @NotNull Integer ssId,
                                     @NotNull SisMonitorTrans sisMonitorTrans) throws IncorrectParameterException, InvalidPermissionException {
        SisMonitorTransKey sisMonitorTransKey = new SisMonitorTransKey();
        sisMonitorTransKey.setSsId(ssId);
        sisMonitorTransKey.setSmtWeek(sisMonitorTrans.getSmtWeek());
        SisMonitorTrans stdSisMonitorTrans = Optional
            .ofNullable(sisMonitorTransMapper.selectByPrimaryKey(sisMonitorTransKey))
            .orElseThrow(() ->
                new IncorrectParameterException("Incorrect sisMonitorTrans: " + new JSONObject(sisMonitorTransKey).toString()));

        if (!stdSisMonitorTrans.getSuId().equals(sisUser.getSuId()))
            throw new InvalidPermissionException(
                "Invalid Permission: sisMonitorTrans " + new JSONObject(sisMonitorTransKey).toString());

        stdSisMonitorTrans.setSmtStatus(sisMonitorTrans.getSmtStatus());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisMonitorTransMapper.updateByPrimaryKey(stdSisMonitorTrans));
        return jsonObject;
    }
}

