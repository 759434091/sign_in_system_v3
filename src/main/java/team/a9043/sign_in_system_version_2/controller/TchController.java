package team.a9043.sign_in_system_version_2.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.SignIn;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;
import team.a9043.sign_in_system_version_2.service.AdmService;
import team.a9043.sign_in_system_version_2.service.SuvService;
import team.a9043.sign_in_system_version_2.service.TchService;
import team.a9043.sign_in_system_version_2.tokenuser.TokenUser;

import java.time.LocalDateTime;
import java.util.List;

import static team.a9043.sign_in_system_version_2.controller.SuvController.getSignIn;

@RestController
@RequestMapping("/teacher")
public class TchController {
    private TchService tchService;
    private AdmService admService;
    private SuvService suvService;

    @Autowired
    public TchController(TchService tchService, AdmService admService) {
        this.tchService = tchService;
        this.admService = admService;
    }

    @RequestMapping(value = "/courses", method = RequestMethod.GET)
    public List<Course> showTchCourses(@TokenUser User user) throws ParameterNotFoundException {
        return tchService.showTchCourses(user);
    }


    @RequestMapping(value = "/course/students", method = RequestMethod.GET)
    public List<User> getCozStudent(@RequestParam("cozId") String cozId) throws ParameterNotFoundException {
        return admService.getCozStudent(cozId);
    }


    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public JSONArray getCozHistorySignIn(@RequestParam("cozId") String cozId) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getCozHistorySignIn(cozId, currentDateTime);
    }

    /**
     * 获得签到设置
     *
     * @param suvWeek       督导周
     * @param scheduleSchId 排课号
     * @return 签到设置
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/sign-in", method = RequestMethod.GET)
    public SignIn getSuvSignIn(@RequestParam("suvWeek") int suvWeek, @RequestParam("scheduleSchId") int scheduleSchId) throws ParameterNotFoundException {
        return getSignIn(suvWeek, scheduleSchId, admService);
    }

    /**
     * 更新签到设置
     *
     * @param signIn 签到设置
     * @return 设置状态
     * @throws ParameterNotFoundException 缺少参数
     */
    @RequestMapping(value = "/sign-in", method = RequestMethod.POST)
    public JSONObject changeSignIn(@RequestBody SignIn signIn) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        return changeSignInDtl(signIn, currentDateTime, jsonObject, admService);
    }

    @RequestMapping(value = "/suv-history", method = RequestMethod.GET)
    public List<SuvRecWithSuv> getCozSuvRec(@RequestParam String cozId) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getCozSuvRec(cozId, currentDateTime);
    }

    static JSONObject changeSignInDtl(@RequestBody SignIn signIn, LocalDateTime currentDateTime, JSONObject jsonObject, AdmService admService) throws ParameterNotFoundException {
        if (admService.changeSignIn(signIn, currentDateTime)) {
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
