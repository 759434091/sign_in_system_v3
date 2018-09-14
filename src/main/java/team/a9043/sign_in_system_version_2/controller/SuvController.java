package team.a9043.sign_in_system_version_2.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system_version_2.exception.InsufficientPermissionsException;
import team.a9043.sign_in_system_version_2.exception.InvalidParameterException;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInRecOnLeaveWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;
import team.a9043.sign_in_system_version_2.service.AdmService;
import team.a9043.sign_in_system_version_2.service.SuvService;
import team.a9043.sign_in_system_version_2.tokenuser.TokenUser;
import team.a9043.sign_in_system_version_2.util.PowerOperation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/supervisor")
public class SuvController {
    private final SuvService suvService;
    private final AdmService admService;

    @Autowired
    public SuvController(SuvService suvService, AdmService admService) {
        this.suvService = suvService;
        this.admService = admService;
    }

    /**
     * 获得待领取督导池
     *
     * @return 督导课程列表
     */
    @RequestMapping(value = "/supervision-pool", method = RequestMethod.GET)
    public JSONArray getToBeSupervised() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return suvService.getSuvPool(currentDateTime);
    }

    /**
     * 督导历史记录
     *
     * @param suvId 督导号
     * @return 督导历史
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public SuvRecWithSuv getSuvRec(@RequestParam int suvId) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Supervision supervision = new Supervision();
        supervision.setSuvId(suvId);
        return suvService.getSuvRec(currentDateTime, supervision);
    }

    /**
     * 获得该督导员督导课程
     *
     * @param user 督导员
     * @return 督导课程列表
     */
    @RequestMapping(value = "/supervisions", method = RequestMethod.GET)
    public List<Supervision> getSuvCourseList(@TokenUser User user, @RequestParam(value = "suvWeek", required = false) Integer suvWeek) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return suvService.getSuvCourseList(user, currentDateTime, suvWeek);
    }

    /**
     * 插入督导记录
     *
     * @param user   督导员
     * @param suvRec 督导记录
     * @return 插入状态
     * @throws InsufficientPermissionsException 权限不足
     * @throws ParameterNotFoundException       参数不足
     */
    @RequestMapping(value = "/supervisions", method = RequestMethod.POST)
    public JSONObject insertSuvRec(@TokenUser User user, @RequestBody SuvRec suvRec) throws InsufficientPermissionsException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.insertSuvRec(user, suvRec, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 放弃督导权
     *
     * @param user        督导员
     * @param supervision 督导设置
     * @return 操作状态
     * @throws InsufficientPermissionsException 权限不足
     * @throws ParameterNotFoundException       参数不足
     */
    @RequestMapping(value = "/supervisions", method = RequestMethod.DELETE)
    public JSONObject giveUpPower(@TokenUser User user, @RequestBody Supervision supervision) throws InsufficientPermissionsException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.changePower(user, PowerOperation.GIVE_UP, supervision, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 领取督导池的督导设置
     *
     * @param user 督导员
     * @return 操作状态
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/supervisions", method = RequestMethod.PATCH)
    public JSONObject acceptPower(@TokenUser User user, @RequestBody Map paramList) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.receiveSuvSchedule(currentDateTime, user, Integer.valueOf(paramList.get("schId").toString()), (ArrayList) paramList.get("suvWeeks"))) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 获得该督导员管理的请假记录
     *
     * @param user 督导员
     * @return 请假记录列表
     */
    @RequestMapping(value = "/stu-leaves", method = RequestMethod.GET)
    public List<SignInRecOnLeaveWithCozDtl> getStuLeaveRec(@TokenUser User user) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return suvService.getStuLeaveRec(user, currentDateTime);
    }

    /**
     * 审批请假
     *
     * @param user      督导员
     * @param signInRec 一条请假记录
     * @return 操作状态
     * @throws ParameterNotFoundException       参数不足
     * @throws InsufficientPermissionsException 权限不足
     * @throws InvalidParameterException        非法参数
     */
    @RequestMapping(value = "/stu-leaves", method = RequestMethod.POST)
    public JSONObject approveOrRejectLeave(@TokenUser User user, @RequestBody SignInRec signInRec) throws ParameterNotFoundException, InsufficientPermissionsException, InvalidParameterException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.approveOrRejectLeave(user, signInRec, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 获得该督导的签到设置
     *
     * @param suvWeek       督导周
     * @param scheduleSchId 排课号
     * @return 签到设置
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/suv-sign-in", method = RequestMethod.GET)
    public SignIn getSuvSignIn(@RequestParam("suvWeek") int suvWeek, @RequestParam("scheduleSchId") int scheduleSchId) throws ParameterNotFoundException {
        return getSignIn(suvWeek, scheduleSchId, admService);
    }

    static SignIn getSignIn(int suvWeek, int scheduleSchId, AdmService admService) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Supervision supervision = new Supervision();
        supervision.setSuvWeek(suvWeek);
        ScheduleWithCozDtl scheduleWithCozDtl = new ScheduleWithCozDtl();
        scheduleWithCozDtl.setSchId(scheduleSchId);
        supervision.setScheduleWithCozDtl(scheduleWithCozDtl);
        return admService.getSignInBySchAndWeek(supervision, currentDateTime);
    }
    /**
     * 更新签到设置
     *
     * @param user   督导员
     * @param signIn 签到设置
     * @return 设置状态
     * @throws InsufficientPermissionsException 权限不足
     * @throws ParameterNotFoundException       缺少参数
     */
    @RequestMapping(value = "/suv-sign-in", method = RequestMethod.POST)
    public JSONObject changeSignIn(@TokenUser User user, @RequestBody SignIn signIn) throws InsufficientPermissionsException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.changeSignIn(user, signIn, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 申请督导转接
     *
     * @param user     督导员
     * @param suvTrans 督导转接
     * @return 申请状态
     * @throws InsufficientPermissionsException 权限不足
     * @throws ParameterNotFoundException       参数不足
     */
    @RequestMapping(value = "/trans-supervision", method = RequestMethod.PUT)
    public JSONObject insertSuvTrans(@TokenUser User user, @RequestBody SuvTrans suvTrans) throws InsufficientPermissionsException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.insertSuvTrans(user, suvTrans, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 获得自己督导转接表
     *
     * @param user 督导员
     * @return 督导转接表
     */
    @RequestMapping(value = "/trans-supervision", method = RequestMethod.GET)
    public List<SuvTrans> getSuvTrans(@TokenUser User user) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return suvService.getSuvTrans(user, currentDateTime);
    }

    /**
     * 拒绝督导转接申请
     *
     * @param user     督导员
     * @param suvTrans 督导转接
     * @return 拒绝状态
     * @throws InsufficientPermissionsException 权限不足
     * @throws ParameterNotFoundException       参数不足
     */
    @RequestMapping(value = "/trans-supervision", method = RequestMethod.DELETE)
    public JSONObject rejectSuvTrans(@TokenUser User user, @RequestBody SuvTrans suvTrans) throws InsufficientPermissionsException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.rejectSuvTrans(user, suvTrans, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 接受督导转接申请
     *
     * @param user     督导员
     * @param suvTrans 督导转接
     * @return 接受状态
     * @throws InsufficientPermissionsException 权限不足
     * @throws ParameterNotFoundException       参数不足
     */
    @RequestMapping(value = "/trans-supervision", method = RequestMethod.POST)
    public JSONObject acceptSuvTrans(@TokenUser User user, @RequestBody SuvTrans suvTrans) throws InsufficientPermissionsException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (suvService.acceptSuvTrans(user, suvTrans, currentDateTime)) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }
}
