package team.a9043.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.JavaUtil.TransSchedule;
import team.a9043.mvcaop.TokenUser;
import team.a9043.pojo.*;
import team.a9043.service.CourseService;
import team.a9043.service.SignInService;

import java.time.LocalDateTime;
import java.util.List;

@Controller("stuController")
@RequestMapping("/Student")
public class StuController {

    private final CourseService courseService;
    private final SignInService signInService;

    @Autowired
    public StuController(CourseService courseService, SignInService signInService) {
        this.courseService = courseService;
        this.signInService = signInService;
    }

    /**
     * 列出该学生用户所有课程
     *
     * @return 返回 一学生所有课程 对象
     */
    @RequestMapping("/showCourses")
    @ResponseBody
    public JSONObject showCourses(@TokenUser User user) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(courseService.showCourses(user.getUserId()));
        jsonObject.put("currentWeek", TransSchedule.getWeek(LocalDateTime.now()));
        return jsonObject;
    }

    /**
     * 签到
     *
     * @return 返回成功与否
     */
    @ResponseBody
    @RequestMapping("/signIn")
    public String signIn(@TokenUser User user, @RequestBody Schedule schedule) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return signInService.getSignInRes(user, schedule, localDateTime);
    }

    /**
     * 请假
     *
     * @return 返回成功与否
     */
    @ResponseBody
    @RequestMapping("/leave")
    public Boolean leave(@TokenUser User user, @RequestPart("voucher") MultipartFile voucher, @RequestPart("schedule") String scheduleStr) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Schedule schedule = JSON.parseObject(scheduleStr, Schedule.class);
        return signInService.getLeaveRes(user, voucher, schedule, localDateTime);
    }

    /**
     * 查看正上的课程
     *
     * @return 一个课程信息
     */
    @ResponseBody
    @RequestMapping("/checkCourse")
    public OneCozAndSch checkCourse(@TokenUser User user) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return courseService.checkCourse(user, localDateTime);
    }

    @ResponseBody
    @RequestMapping("/fOneCozSignIn")
    public List<SignInRes> fOneCozSignIn(@TokenUser User user, @RequestBody Schedule schedule) {
        return courseService.fOneCozSignIn(user, schedule);
    }

    @ResponseBody
    @RequestMapping("/fOneCozAbsent")
    public int[] fOneCozAbsent(@TokenUser User user, @RequestBody Schedule schedule) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return courseService.fOneCozAbsent(user, schedule, localDateTime);
    }

    /**
     * 检查是否已经签到
     *
     * @return 返回是否
     */
    @ResponseBody
    @RequestMapping("/checkSignIn")
    public Boolean checkSignIn(@TokenUser User user, @RequestBody Schedule schedule) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return signInService.checkSignIn(user, schedule, localDateTime);
    }

}
