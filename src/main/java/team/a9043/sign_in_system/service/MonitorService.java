package team.a9043.sign_in_system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system.async.AsyncJoinService;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.mapper.*;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;
import team.a9043.sign_in_system.util.judgetime.ScheduleParserException;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author a9043
 */
@Service
@Slf4j
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
    private SisUserInfoMapper sisUserInfoMapper;
    @Resource
    private AsyncJoinService asyncJoinService;
    private static final int N = 20;
    private ReentrantLock[] locks = new ReentrantLock[N];

    {
        for (int i = 0; i < N; i++) {
            locks[i] = new ReentrantLock();
        }
    }


    public PageInfo<SisCourse> getCourses(@NotNull SisUser sisUser) throws ExecutionException, InterruptedException {
        SisCourseExample sisCourseExample = new SisCourseExample();
        sisCourseExample.createCriteria().andSuIdEqualTo(sisUser.getSuId());

        List<SisCourse> sisCourseList =
            sisCourseMapper.selectByExample(sisCourseExample);
        PageInfo<SisCourse> sisCoursePageInfo = new PageInfo<>(sisCourseList);
        if (sisCourseList.isEmpty()) return sisCoursePageInfo;

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
        sisJoinCourseList.forEach(j -> j.setSisUser(sisUserList.parallelStream()
            .filter(u -> u.getSuId().equals(j.getSuId()))
            .peek(u -> u.setSuPassword(null))
            .findAny()
            .orElse(null)));

        //merge sisCourse
        sisCoursePageInfo.getList().forEach(c -> {
            c.setSisScheduleList(sisScheduleList.stream()
                .filter(sisSchedule -> sisSchedule.getScId().equals(c.getScId()))
                .collect(Collectors.toList()));

            c.setSisJoinCourseList(sisJoinCourseList.parallelStream()
                .filter(j -> j.getScId().equals(c.getScId()))
                .collect(Collectors.toList()));
        });

        if (log.isDebugEnabled()) {
            log.debug("User " + sisUser.getSuId() + " get course. ");
        }
        return sisCoursePageInfo;
    }

    public JSONObject getSupervisions(@NotNull SisUser sisUser,
                                      @NotNull String scId) throws IncorrectParameterException, InvalidPermissionException {

        SisCourse sisCourse = Optional
            .ofNullable(sisCourseMapper.selectByPrimaryKey(scId))
            .orElseThrow(() -> new IncorrectParameterException("No course: " + scId));

        if (!sisUser.getSuAuthorities().contains(new SimpleGrantedAuthority(
            "ADMINISTRATOR")) &&
            !sisUser.getSuAuthorities().contains(new SimpleGrantedAuthority(
                "TEACHER")) &&
            !sisUser.getSuId().equals(sisCourse.getSuId())) {
            throw new InvalidPermissionException(
                "Invalid permission: " + scId);
        }

        //join schedule
        SisScheduleExample sisScheduleExample = new SisScheduleExample();
        sisScheduleExample.createCriteria().andScIdEqualTo(scId);
        List<SisSchedule> sisScheduleList =
            sisScheduleMapper.selectByExample(sisScheduleExample);

        List<Integer> ssIdList = sisScheduleList.stream()
            .map(SisSchedule::getSsId)
            .collect(Collectors.toList());

        //join supervision
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
        if (log.isDebugEnabled()) {
            log.debug("User " + sisUser.getSuId() + " get supervisions: " + scId);
        }
        return jsonObject;
    }

    @Transactional
    public JSONObject drawMonitor(@NotNull SisUser sisUser,
                                  @NotNull String scId) throws IncorrectParameterException, InvalidPermissionException {
        int idx = String.format("sis_draw_monitor_%s", scId).hashCode() % N;
        if (idx < 0)
            idx = idx + N;
        ReentrantLock lock =
            locks[idx];
        if (!lock.tryLock()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "该课已被占, 请稍后再试");
            return jsonObject;
        }

        SisCourse sisCourse = Optional
            .ofNullable(sisCourseMapper.selectByPrimaryKey(scId))
            .orElseThrow(() -> new IncorrectParameterException("No course" +
                ": " + scId));

        if (null != sisCourse.getSuId()) {
            throw new InvalidPermissionException("Invalid permission: " + scId);
        }

        SisCourse updatedSisCourse = new SisCourse();
        updatedSisCourse.setScId(sisCourse.getScId());
        updatedSisCourse.setSuId(sisUser.getSuId());

        JSONObject jsonObject = new JSONObject();
        boolean success =
            sisCourseMapper.updateByPrimaryKeySelective(updatedSisCourse) > 0;
        jsonObject.put("success", success);
        log.info("User " + sisUser.getSuId() + " has draw course: " + scId);
        lock.unlock();
        return jsonObject;
    }

    @Transactional
    public JSONObject insertSupervision(@NotNull SisUser sisUser,
                                        @NotNull Integer ssId,
                                        @NotNull SisSupervision sisSupervision,
                                        @NotNull LocalDateTime currentDateTime) throws IncorrectParameterException, ScheduleParserException, InvalidPermissionException, InvalidTimeParameterException {
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

        //valid suspension
        Integer ssvWeek = sisSupervision.getSsvWeek();
        boolean isSuspend = sisSchedule.getSsSuspensionList()
            .stream()
            .anyMatch(week -> week.equals(ssvWeek));
        if (isSuspend)
            throw new InvalidPermissionException(String.format(
                "Schedule %d week %d is in the suspension list",
                ssId, ssvWeek));

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
                    sisMonitorTrans.getSmtStatus().equals(SisMonitorTrans.SmtStatus.AGREE.ordinal()) &&
                        sisUser.getSuId().equals(sisMonitorTrans.getSuId()))
                .orElseThrow(() ->
                    new InvalidPermissionException("No permission: " + sisSchedule.getSsId()));
        }

        //judge time
        if (!JudgeTimeUtil.isCourseTime(sisSchedule,
            sisSupervision.getSsvWeek(),
            currentDateTime)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "Incorrect time");
            return jsonObject;
        }

        sisSupervision.setSsId(ssId);
        JSONObject jsonObject = new JSONObject();
        boolean success = sisSupervisionMapper.insert(sisSupervision) > 0;
        jsonObject.put("success", success);
        log.info("User " + sisUser.getSuId() + " insert supervision: ssId " + ssId + " week " + ssvWeek);
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
        Integer smtWeek = sisMonitorTrans.getSmtWeek();
        boolean isSuspend = sisSchedule.getSsSuspensionList()
            .stream()
            .anyMatch(week -> week.equals(smtWeek));
        if (isSuspend)
            throw new InvalidPermissionException(String.format(
                "Schedule %d week %d is in the suspension list",
                ssId, smtWeek));

        //check exist
        SisMonitorTransKey sisMonitorTransKey = new SisMonitorTransKey();
        sisMonitorTransKey.setSsId(ssId);
        sisMonitorTransKey.setSmtWeek(sisMonitorTrans.getSmtWeek());
        SisMonitorTrans stdSisMonitorTrans =
            sisMonitorTransMapper.selectByPrimaryKey(sisMonitorTransKey);
        if (null != stdSisMonitorTrans) {
            throw new InvalidPermissionException("MonitorTrans exist: " + new JSONObject(sisMonitorTransKey).toString());
        }

        //check permission
        SisCourse sisCourse =
            sisCourseMapper.selectByPrimaryKey(sisSchedule.getScId());
        if (null == sisCourse || null == sisCourse.getSuId() || !sisCourse.getSuId().equals(sisUser.getSuId())) {
            throw new InvalidPermissionException("Invalid Permission: ssId " + sisSchedule.getSsId());
        }

        sisMonitorTrans.setSmtStatus(SisMonitorTrans.SmtStatus.UNTREATED.ordinal());
        sisMonitorTrans.setSuId(sisMonitorTrans.getSuId());
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
        if (null == sisMonitorTrans.getSmtWeek())
            throw new IncorrectParameterException("Incorrect smtWeek: " + sisMonitorTrans.getSmtWeek());
        if (null == sisMonitorTrans.getSmtStatus())
            throw new IncorrectParameterException("Incorrect smtStatus: " + sisMonitorTrans.getSmtStatus());
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

    @SuppressWarnings("Duplicates")
    public JSONObject getMonitors(@Nullable Integer page,
                                  @Nullable Integer pageSize,
                                  @Nullable String suId,
                                  @Nullable String suName,
                                  @Nullable Boolean ordByLackNum) throws IncorrectParameterException {
        if (null == page) {
            throw new IncorrectParameterException("Incorrect page: " + null);
        }
        if (page < 1)
            throw new IncorrectParameterException("Incorrect page: " + page +
                " (must equal or bigger than 1)");
        if (null == pageSize)
            pageSize = 10;
        else if (pageSize <= 0 || pageSize > 500) {
            throw new IncorrectParameterException("pageSize must between [1," +
                "500]");
        }

        SisUserExample sisUserExample = new SisUserExample();
        SisUserExample.Criteria criteria = sisUserExample.createCriteria();
        criteria.andSuAuthoritiesStrLike("%MONITOR%");

        if (null != suId) {
            criteria.andSuIdLike("%" + suId + "%");
        }
        if (null != suName) {
            criteria.andSuNameLike(CourseService.getFuzzySearch(suName));
        }
        if (null != ordByLackNum && ordByLackNum) {
            sisUserExample.setOrderByClause("sui_lack_num desc");
            sisUserExample.setOrdByLackNum(true);
        }

        PageHelper.startPage(page, pageSize);
        List<SisUser> sisUserList =
            sisUserMapper.selectByExample(sisUserExample);
        PageInfo<SisUser> pageInfo = new PageInfo<>(sisUserList);

        List<String> suIdList = sisUserList.parallelStream()
            .map(SisUser::getSuId)
            .distinct()
            .collect(Collectors.toList());

        if (suIdList.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("page", page);
            jsonObject.put("message", "No monitor");
            return jsonObject;
        }

        SisUserInfoExample sisUserInfoExample = new SisUserInfoExample();
        sisUserInfoExample.createCriteria().andSuIdIn(suIdList);
        List<SisUserInfo> sisUserInfoList =
            sisUserInfoMapper.selectByExample(sisUserInfoExample);

        JSONObject pageJson = new JSONObject(pageInfo);
        pageJson.getJSONArray("list")
            .forEach(sisUserObj -> {
                JSONObject sisUserJson = (JSONObject) sisUserObj;
                String tSuId = sisUserJson.getString("suId");

                sisUserJson.remove("suPassword");
                sisUserJson.put("suiLackNum", sisUserInfoList.parallelStream()
                    .filter(sisUserInfo -> sisUserInfo.getSuId().equals(tSuId))
                    .findAny()
                    .map(SisUserInfo::getSuiLackNum)
                    .orElse(0));
            });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("page", page);
        jsonObject.put("data", pageJson);
        return jsonObject;
    }

    public JSONObject grantMonitor(String suId) throws IncorrectParameterException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser)
            throw new IncorrectParameterException("Incorrect suId: " + suId);

        GrantedAuthority monitorAuth = new SimpleGrantedAuthority("MONITOR");
        List<GrantedAuthority> authList = sisUser.getSuAuthorities();
        if (!authList.contains(monitorAuth))
            authList.add(monitorAuth);

        SisUser updatedSisUser = new SisUser();
        updatedSisUser.setSuId(sisUser.getSuId());
        updatedSisUser.setSuAuthorities(authList);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisUserMapper.updateByPrimaryKeySelective(updatedSisUser));
        return jsonObject;
    }

    public JSONObject revokeMonitor(String suId) throws IncorrectParameterException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey(suId);
        if (null == sisUser)
            throw new IncorrectParameterException("Incorrect suId: " + suId);

        GrantedAuthority monitorAuth = new SimpleGrantedAuthority("MONITOR");
        List<GrantedAuthority> authList = sisUser.getSuAuthorities();
        authList.remove(monitorAuth);

        SisUser updatedSisUser = new SisUser();
        updatedSisUser.setSuId(sisUser.getSuId());
        updatedSisUser.setSuAuthorities(authList);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            sisUserMapper.updateByPrimaryKeySelective(updatedSisUser));
        return jsonObject;
    }
}

