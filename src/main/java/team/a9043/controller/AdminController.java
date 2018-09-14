package team.a9043.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.a9043.pojo.*;
import team.a9043.service.AdminService;
import team.a9043.service.CourseService;
import team.a9043.service.LoginService;

import java.util.List;
import java.util.ResourceBundle;

@Controller("adminController")
@RequestMapping("/Administrator")
public class AdminController {
    final private AdminService adminService;
    final private CourseService courseService;
    private final static int PAGE_SIZE = returnPageNum();

    @Autowired
    public AdminController(AdminService adminService, CourseService courseService, LoginService loginService) {
        this.adminService = adminService;
        this.courseService = courseService;
    }

    private static int returnPageNum() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pageSetting");
        return Integer.valueOf(resourceBundle.getString("pageSize"));
    }

    @ResponseBody
    @RequestMapping("/findSuv")
    public List<User> findSuv() {
        return adminService.findSuv();
    }


    @ResponseBody
    @RequestMapping("/findSuvSch")
    public List<SuvSch> findSuvSch(@RequestBody User user) {
        return adminService.findSuvSch(user);
    }

    @ResponseBody
    @RequestMapping("/showCourses")
    public OneStuSchedule showCourses(@RequestBody User user) {
        return adminService.showCourses(user);
    }

    @ResponseBody
    @RequestMapping("/grantSuv")
    public boolean grantSuv(@RequestBody User user) {
        return adminService.grantSuv(user);
    }

    @ResponseBody
    @RequestMapping("/revokeSuv")
    public boolean revokeSuv(@RequestBody User user) {
        return adminService.revokeSuv(user);
    }

    @ResponseBody
    @RequestMapping("/getAllCourseNumber")
    public JSONObject getAllCourseNumber(@RequestParam(value = "cozName", required = false) String cozName,
                                         @RequestParam(value = "cozDepart", required = false) String cozDepart,
                                         @RequestParam(value = "cozGrade", required = false) Integer cozGrade) {
        int countNum = adminService.getAllCourseNumber(cozName, cozDepart, cozGrade);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("countNum", countNum);
        jsonObject.put("pageSize", PAGE_SIZE);
        jsonObject.put("pageNum", (countNum + PAGE_SIZE - 1) / PAGE_SIZE);
        return jsonObject;
    }

    @RequestMapping("/getCozStudent")
    @ResponseBody
    public List<User> getCozStudent(@RequestParam("cozId") String cozId) {
        return adminService.getCozStudent(cozId);
    }

    @ResponseBody
    @RequestMapping("/showAllCourses")
    public List<AdmCourse> showAllCourses(@RequestParam("page") int page,
                                          @RequestParam(value = "sortStr", required = false) String sortStr,
                                          @RequestParam(value = "isAsc", required = false) Boolean isAsc,
                                          @RequestParam(value = "cozName", required = false) String cozName,
                                          @RequestParam(value = "cozDepart", required = false) String cozDepart,
                                          @RequestParam(value = "cozGrade", required = false) Integer cozGrade) {
        return adminService.showAllCourses(cozName, cozDepart, cozGrade, page, PAGE_SIZE, sortStr, isAsc == null ? true : isAsc);
    }

    @Deprecated
    @ResponseBody
    @RequestMapping("/showOneCozStuList")
    public List<User> showOneCozStuList(@RequestBody Course course) {
        return adminService.showOneCozStuList(course);
    }

    @ResponseBody
    @RequestMapping("/showOneSchRec")
    public OneSchSuvRec showOneSchRec(@RequestBody Schedule schedule) {
        return adminService.showOneSchRec(schedule);
    }

    @ResponseBody
    @RequestMapping("/findHisSuvRecRes")
    public List<HisSuvRecRes> findHisSuvRecRes(@RequestBody String userId) {
        return adminService.findHisSuvRecRes(userId);
    }

    @ResponseBody
    @RequestMapping("/findHisAllRecRes")
    public List<HisSuvRecRes> findHisAllRecRes(@RequestParam("cozId") String cozId) {
        return adminService.findHisAllRecRes(cozId);
    }

    @ResponseBody
    @RequestMapping("/fSchAbsRecByCoz")
    public List<SchAbsRec> fSchAbsRecByCoz(@RequestParam("cozId") String cozId) {
        return adminService.fSchAbsRecByCoz(cozId);
    }

    /**
     * 查看监督课程
     *
     * @param userId 用户
     * @return 返回 课程列表
     */
    @ResponseBody
    @RequestMapping("/showSuvCourses")
    public List<SuvSch> showSuvCourses(@RequestBody String userId) {
        return adminService.showSuvCourses(userId);
    }

    /**
     * 列出该学生用户所有课程
     *
     * @param userId 用户
     * @return 返回 一学生所有课程 对象
     */
    @RequestMapping("/showSuvStuCourses")
    @ResponseBody
    public OneStuSchedule showSuvStuCourses(@RequestBody String userId) {
        return courseService.showCourses(userId);
    }


    @RequestMapping("/updateCozAttRate")
    @ResponseBody
    public int updateCozAttRate() {
        return adminService.updateCozAttRate();
    }

    @RequestMapping("/updateCozNumber")
    @ResponseBody
    public int updateCozNumber() {
        return adminService.updateCozNumber();
    }

    @ResponseBody
    @RequestMapping("/findCozSuv")
    public List<Integer> findCozSuv(@RequestBody int schId) {
        return adminService.findCozSuv(new Schedule().setSchId(schId));
    }

    @ResponseBody
    @RequestMapping("/setCozSuv")
    public boolean setCozSuv(@RequestBody String paramStr) {
        String[] paramStrList = paramStr.split("&");
        SuvSch suvSch = new SuvSch();
        suvSch.setSchedule(new Schedule().setSchId(Integer.valueOf(paramStrList[0])));
        suvSch.setSuvWeek(Integer.valueOf(paramStrList[1]));
        return adminService.setCozSuv(suvSch);
    }

    @ResponseBody
    @RequestMapping("/removeCozSuv")
    public boolean removeCozSuv(@RequestBody String paramStr) {
        String[] paramStrList = paramStr.split("&");
        SuvSch suvSch = new SuvSch();
        suvSch.setSchedule(new Schedule().setSchId(Integer.valueOf(paramStrList[0])));
        suvSch.setSuvWeek(Integer.valueOf(paramStrList[1]));
        return adminService.removeCozSuv(suvSch);
    }

    @ResponseBody
    @RequestMapping("/setCozSignIn")
    public boolean setCozSignIn(@RequestBody SuvMan suvMan) {
        return adminService.setCozSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/getCozSignIn")
    public List<Integer> getCozSignIn(@RequestBody int schId) {
        return adminService.getCozSignIn(new Schedule().setSchId(schId));
    }

    @ResponseBody
    @RequestMapping("/removeCozSignIn")
    public boolean removeCozSignIn(@RequestBody SuvMan suvMan) {
        return adminService.removeCozSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/getLeaves")
    public List<SignInRes> getLeaves(@RequestBody String cozId) {
        return adminService.getLeaves(cozId);
    }

    @ResponseBody
    @RequestMapping("/approveLeave")
    public boolean approveLeave(@RequestBody SignInRes signInRes) {
        return adminService.approveLeave(signInRes);
    }

    @ResponseBody
    @RequestMapping("/rejectLeave")
    public boolean rejectLeave(@RequestBody SignInRes signInRes) {
        return adminService.rejectLeave(signInRes);
    }

    @ResponseBody
    @RequestMapping("/selectAbcStatistics")
    public JSONObject selectAbcStatistics(@RequestParam(value = "week", required = false) Integer week,
                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "cozName", required = false) String cozName,
                                            @RequestParam(value = "cozDepart", required = false) String cozDepart,
                                            @RequestParam(value = "cozGrade", required = false) Integer cozGrade,
                                            @RequestParam(value = "userId", required = false) String userId,
                                            @RequestParam(value = "userName", required = false) String userName) {
        return adminService.selectAbcStatistics(week, page, cozName, cozDepart, cozGrade, userId, userName);
    }
}
