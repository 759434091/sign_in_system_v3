package team.a9043.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.a9043.JavaUtil.TransSchedule;
import team.a9043.mvcaop.TokenUser;
import team.a9043.pojo.*;
import team.a9043.service.LoginService;
import team.a9043.service.TchService;

import java.time.LocalDateTime;
import java.util.List;

@Controller("tchController")
@RequestMapping("/Teacher")
public class TchController {
    private final TchService tchService;

    @Autowired
    public TchController(TchService tchService, LoginService loginService) {
        this.tchService = tchService;
    }

    /**
     * 列出该用户所有课程
     *
     * @return 返回 一学生所有课程 对象
     */
    @RequestMapping("/showTchCourses")
    @ResponseBody
    public JSONObject showTchCourses(@TokenUser User user) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(tchService.showTchCourses(user));
        jsonObject.put("currentWeek", TransSchedule.getWeek(LocalDateTime.now()));
        return jsonObject;
    }

    @RequestMapping("/getCozStudent")
    @ResponseBody
    public List<User> getCozStudent(@RequestParam("cozId") String cozId) {
        return tchService.getCozStudent(cozId);
    }

    /**
     * 历史督导记录
     *
     * @param schedule 排课
     * @return SuvRecord
     */
    @RequestMapping("/fSuvRecByCoz")
    @ResponseBody
    public List<SuvRecord> fSuvRecByCoz(@RequestBody Schedule schedule) {
        return tchService.fSuvRecByCoz(schedule);
    }

    /**
     * 历史缺勤记录
     *
     * @return SchAbsRec
     */
    @RequestMapping("/fSchAbsRecByCoz")
    @ResponseBody
    public List<AbsStatistics> fSchAbsRecByCoz(@RequestParam("schId") Integer schId, @RequestParam("week") Integer week) {
        Schedule schedule = new Schedule();
        schedule.setSchId(schId);
        return tchService.selectAbcStatistics(schedule, week);
    }

    /**
     * 审批请假
     *
     * @param user      用户
     * @param signInRes 排课
     * @return boolean
     */
    @ResponseBody
    @RequestMapping("/approveLeave")
    public Boolean approveLeave(@TokenUser User user, @RequestBody SignInRes signInRes) {
        return tchService.approveLeave(user, signInRes);
    }

    @ResponseBody
    @RequestMapping("/rejectLeave")
    public Boolean rejectLeave(@TokenUser User user, @RequestBody SignInRes signInRes) {
        return tchService.rejectLeave(user, signInRes);
    }

    /**
     * 获得请假记录
     *
     * @return SignInRes
     */
    @ResponseBody
    @RequestMapping("/getLeaves")
    public List<SignInRes> getLeaves(@TokenUser User user) {
        return tchService.getLeaves(user);
    }

    /**
     * 课程停课
     *
     * @param paramStr 字符串
     * @return boolean
     */
    @ResponseBody
    @RequestMapping("/suspendClass")
    public Boolean suspendClass(@TokenUser User user, @RequestBody String paramStr) {
        String[] paramStrList = paramStr.split("&");
        Schedule schedule = new Schedule();
        schedule.setSchId(Integer.valueOf(paramStrList[0]));
        int susWeek = Integer.valueOf(paramStrList[1]);
        return tchService.suspendClass(user, schedule, susWeek);
    }

    @ResponseBody
    @RequestMapping("/findCozSuv")
    public List<Integer> findCozSuv(@RequestBody int schId) {
        return tchService.findCozSuv(new Schedule().setSchId(schId));
    }

    @ResponseBody
    @RequestMapping("/setCozSuv")
    public Boolean setCozSuv(@TokenUser User user, @RequestBody String paramStr) {
        String[] paramStrList = paramStr.split("&");
        SuvSch suvSch = new SuvSch();
        suvSch.setSchedule(new Schedule().setSchId(Integer.valueOf(paramStrList[0])));
        suvSch.setSuvWeek(Integer.valueOf(paramStrList[1]));
        return tchService.setCozSuv(user, suvSch);
    }


    @ResponseBody
    @RequestMapping("/removeCozSuv")
    public Boolean removeCozSuv(@TokenUser User user, @RequestBody String paramStr) {
        String[] paramStrList = paramStr.split("&");
        SuvSch suvSch = new SuvSch();
        suvSch.setSchedule(new Schedule().setSchId(Integer.valueOf(paramStrList[0])));
        suvSch.setSuvWeek(Integer.valueOf(paramStrList[1]));
        return tchService.removeCozSuv(user, suvSch);
    }

    @ResponseBody
    @RequestMapping("/setCozSignIn")
    public Boolean setCozSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return tchService.setCozSignIn(user, suvMan);
    }

    @ResponseBody
    @RequestMapping("/getCozSignIn")
    public List<Integer> getCozSignIn(@RequestBody int schId) {
        return tchService.getCozSignIn(new Schedule().setSchId(schId));
    }

    @ResponseBody
    @RequestMapping("/removeCozSignIn")
    public Boolean removeCozSignIn(@RequestBody SuvMan suvMan) {
        return tchService.removeCozSignIn(suvMan);
    }
}
