package team.a9043.sign_in_system_version_2.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.Schedule;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.StuTimetable;
import team.a9043.sign_in_system_version_2.service.StuService;
import team.a9043.sign_in_system_version_2.tokenuser.TokenUser;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StuController {
    private final StuService stuService;

    @Autowired
    public StuController(StuService stuService) {
        this.stuService = stuService;
    }

    /**
     * 获得课表
     *
     * @param user 学生用户
     * @return 课表
     */
    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public StuTimetable getStuTimetable(@TokenUser User user) {
        return stuService.getStuTimetable(user);
    }

    /**
     * 获得本周将签到课堂列表
     * 已经请假或签到的将不被捕获
     *
     * @param user token user
     * @return 需要签到的签到设置
     */
    @RequestMapping(value = "/sign-in", method = RequestMethod.GET)
    public List<SignInWithCozDtl> getNeedSignInByUser(@TokenUser User user) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return stuService.getNeedSignInByUser(user, currentDateTime);
    }

    /**
     * 签到
     *
     * @param user     学生用户
     * @param schedule 签到的排课 需要 schId, schSignInWeek, location
     * @return 签到成功结果
     */
    @RequestMapping(value = "/sign-in", method = RequestMethod.POST)
    public JSONObject signIn(@TokenUser User user, @RequestBody Schedule schedule) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return stuService.signIn(user, schedule, currentDateTime);
    }

    /**
     * 学生请假
     *
     * @param user        学生用户
     * @param voucher     请假凭证
     * @param scheduleStr schedule的JSON序列化
     * @return jsonObject
     * @throws ParameterNotFoundException 没有所需参数
     */
    @RequestMapping(value = "/leave", method = RequestMethod.POST)
    public JSONObject leave(@TokenUser User user, @RequestPart("voucher") MultipartFile voucher, @RequestPart("schedule") String scheduleStr) throws JSONException, ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Schedule schedule;
        try {
            schedule = (Schedule) JSONObject.stringToValue(scheduleStr);
        } catch (JSONException e) {
            throw new JSONException("cannot transform schedule : " + e.getMessage());
        }
        return stuService.leave(user, voucher, schedule, currentDateTime);
    }

    /**
     * 获得历史签到以及请假
     *
     * @param user  学生用户
     * @param cozId 课程Id
     * @return JSONArray
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public JSONArray getSignInAndAbs(@TokenUser User user, @RequestParam("cozId") String cozId) throws ParameterNotFoundException {
        Course course = new Course();
        course.setCozId(cozId);
        LocalDateTime currentDateTime = LocalDateTime.now();
        return stuService.getHistorySignIn(user, course, currentDateTime);
    }
}
