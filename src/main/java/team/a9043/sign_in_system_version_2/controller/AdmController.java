package team.a9043.sign_in_system_version_2.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.SignIn;
import team.a9043.sign_in_system_version_2.pojo.Supervision;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.pojo.extend.StuTimetable;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;
import team.a9043.sign_in_system_version_2.service.AdmService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static team.a9043.sign_in_system_version_2.controller.SuvController.getSignIn;
import static team.a9043.sign_in_system_version_2.controller.TchController.changeSignInDtl;

@RestController
@RequestMapping("/administrator")
public class AdmController {
    private final AdmService admService;
    private final RedisTemplate redisTemplate;

    @Autowired
    public AdmController(AdmService admService, RedisTemplate redisTemplate) {
        this.admService = admService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获得所有督导员
     *
     * @return 督导员
     */
    @RequestMapping(value = "/suv-manage/supervisors", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public List<User> getSupervisor() {
        return admService.getSupervisor();
    }

    /**
     * 获得一周所有督导课程
     *
     * @return 督导课程列表
     */
    @RequestMapping(value = "/suv-manage/supervisions", method = RequestMethod.GET)
    public List<Supervision> getSupervision(@RequestParam(value = "suvWeek", required = false) Integer suvWeek) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getSupervision(currentDateTime, suvWeek);
    }

    /**
     * 获得历史督导记录
     *
     * @param suvId 督导号
     * @return 督导记录
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/suv-manage/suv-history", method = RequestMethod.GET)
    public SuvRecWithSuv getSuvHistory(@RequestParam int suvId) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Supervision supervision = new Supervision();
        supervision.setSuvId(suvId);
        return admService.getSuvRec(currentDateTime, supervision);
    }

    /**
     * 督导员课表
     *
     * @param usrId 督导员学号
     * @return 学生课表
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/suv-manage/course", method = RequestMethod.GET)
    public StuTimetable getStuTimetable(@RequestParam String usrId) throws ParameterNotFoundException {
        return admService.getStuTimetable(usrId);
    }

    /**
     * 新建督导
     *
     * @param supervisionList 督导内容
     * @return 操作信息
     */
    @RequestMapping(value = "/suv-manage/supervisions", method = RequestMethod.PUT)
    public JSONObject insertSupervision(@RequestBody List<Supervision> supervisionList) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (admService.insertSupervision(supervisionList, currentDateTime)) {
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
     * 取消督导
     *
     * @param supervision 督导
     * @return 状态
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/suv-manage/supervisions", method = RequestMethod.DELETE)
    public JSONObject deleteSupervision(@RequestBody Supervision supervision) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        if (admService.deleteSupervision(supervision, currentDateTime)) {
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
     * 授予督导员
     *
     * @param user 该学生
     * @return 状态
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/suv-manage/supervisors", method = RequestMethod.PUT)
    public JSONObject grantSupervisor(@RequestBody User user) throws ParameterNotFoundException {
        JSONObject jsonObject = new JSONObject();
        if (admService.grantSupervisor(user.getUsrId())) {
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
     * 撤回督导员
     *
     * @param user 该学生
     * @return 状态
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/suv-manage/supervisors", method = RequestMethod.DELETE)
    public JSONObject revokeSupervisor(@RequestBody User user) throws ParameterNotFoundException {
        JSONObject jsonObject = new JSONObject();
        if (admService.revokeSupervisor(user.getUsrId())) {
            jsonObject.put("msg", "success");
            jsonObject.put("status", true);
            return jsonObject;
        } else {
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    @RequestMapping(value = "/course/count", method = RequestMethod.GET)
    public JSONObject getCourseCount(@RequestParam(required = false) String cozName,
                                     @RequestParam(required = false) Integer grade,
                                     @RequestParam(required = false) String depName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", admService.getCourseCount(cozName, grade, depName));
        return jsonObject;
    }

    @RequestMapping(value = "/course/courses", method = RequestMethod.GET)
    public List<Course> getCourse(@RequestParam(required = false) String cozName,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer pageSize,
                                  @RequestParam(required = false) String sortStr,
                                  @RequestParam(required = false) Boolean isAsc,
                                  @RequestParam(required = false) Integer grade,
                                  @RequestParam(required = false) String depName) {
        return admService.getCourse(cozName, page, pageSize, sortStr, isAsc, grade, depName);
    }


    @RequestMapping(value = "/course/supervisions", method = RequestMethod.GET)
    public List<Integer> getCourseSupervision(@RequestParam String cozId, @RequestParam Integer week) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getCourseSupervision(cozId, week, currentDateTime);
    }

    @RequestMapping(value = "/course/student", method = RequestMethod.GET)
    public List<User> getCozStudent(@RequestParam String cozId) throws ParameterNotFoundException {
        return admService.getCozStudent(cozId);
    }

    @RequestMapping(value = "/course/suv-history", method = RequestMethod.GET)
    public List<SuvRecWithSuv> getCozSuvRec(@RequestParam String cozId) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getCozSuvRec(cozId, currentDateTime);
    }

    @RequestMapping(value = "/course/sign-in-history", method = RequestMethod.GET)
    public JSONArray getHistorySignIn(@RequestParam String cozId) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getCozHistorySignIn(cozId, currentDateTime);
    }

    /**
     * 获得该督导的签到设置
     *
     * @param suvWeek       督导周
     * @param scheduleSchId 排课号
     * @return 签到设置
     * @throws ParameterNotFoundException 参数不足
     */
    @RequestMapping(value = "/course/suv-sign-in", method = RequestMethod.GET)
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
    @RequestMapping(value = "/course/suv-sign-in", method = RequestMethod.POST)
    public JSONObject changeSignIn(@RequestBody SignIn signIn) throws ParameterNotFoundException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        return changeSignInDtl(signIn, currentDateTime, jsonObject, admService);
    }

    @RequestMapping(value = "/sign-in", method = RequestMethod.GET)
    public JSONArray getSignInByWeek(@RequestParam(value = "siWeek", required = false) Integer siWeek) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return admService.getSignInByWeek(siWeek, currentDateTime);
    }

    @RequestMapping(value = "/department", method = RequestMethod.GET)
    public JSONArray searchDepartment(@RequestParam("depStr") String depStr) {
        JSONArray jsonArray = new JSONArray();
        admService.searchDepartment(depStr).forEach(str -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", str);
            jsonArray.put(jsonObject);
        });
        return jsonArray;
    }

    @RequestMapping(value = "/statistics/att-rate", method = RequestMethod.GET)
    public JSONObject getStaAttRate() {
        return admService.getStaAttRate();
    }

    @RequestMapping(value = "/statistics/abs-rank", method = RequestMethod.GET)
    public JSONObject getAbsRankList(@RequestParam(required = false) Integer limitNum) {
        return admService.getAbsRankList(limitNum);
    }

    /**
     * 课程文件上传
     *
     * @param cozInfo 课程文件
     * @return 上传任务
     */
    @RequestMapping(value = "/file/coz-info", method = RequestMethod.PUT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject courseInfoUpload(MultipartFile cozInfo) {
        JSONObject jsonObject = new JSONObject();
        String taskKey = "task_" + UUID.randomUUID().toString().replace("-", "");

        if (cozInfo == null) {
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            jsonObject.put("info", "course");
            jsonObject.put("taskKey", taskKey);
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }


        redisTemplate.opsForValue().set(taskKey, 5, 5, TimeUnit.MINUTES);
        String fileName = cozInfo.getOriginalFilename();
        File dir = new File(System.getProperty("user.dir") + "/../tempImport");
        File file = new File(dir.getAbsolutePath() + File.separator + fileName);
        try {
            if (!file.getParentFile().exists()) {
                boolean res = file.getParentFile().mkdirs();
                if (!res) {
                    return null;
                }
            }
            for (int i = 1; file.exists(); i++) {
                file = new File(dir.getAbsolutePath() + File.separator + i + "_temp_" + fileName);
            }
            cozInfo.transferTo(file);
            redisTemplate.opsForValue().set(taskKey, 10, 5, TimeUnit.MINUTES);

            admService.readCozExcel(file, taskKey);

            jsonObject.put("taskKey", taskKey);
            jsonObject.put("msg", "成功上传, 数据库正在录入");
            jsonObject.put("status", true);
            return jsonObject;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            jsonObject.put("info", "course");
            jsonObject.put("taskKey", taskKey);
            jsonObject.put("msg", e.getMessage());
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 获取上传进度
     *
     * @return 上传进度
     */
    @RequestMapping(value = "/file/coz-info", method = RequestMethod.GET)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject getCozInfoUploadStatus(@RequestParam String taskKey) {
        return getUploadStatus(taskKey);
    }

    @RequestMapping(value = "/file/stu-info", method = RequestMethod.PUT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject studentInfoUpload(MultipartFile stuInfo) {
        JSONObject jsonObject = new JSONObject();
        String taskKey = "task_" + UUID.randomUUID().toString().replace("-", "");

        if (stuInfo == null) {
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            jsonObject.put("info", "student");
            jsonObject.put("taskKey", taskKey);
            jsonObject.put("msg", "error");
            jsonObject.put("status", false);
            return jsonObject;
        }


        redisTemplate.opsForValue().set(taskKey, 5, 5, TimeUnit.MINUTES);
        String fileName = stuInfo.getOriginalFilename();
        File dir = new File(System.getProperty("user.dir") + "/../tempImport");
        File file = new File(dir.getAbsolutePath() + File.separator + fileName);
        try {
            if (!file.getParentFile().exists()) {
                boolean res = file.getParentFile().mkdirs();
                if (!res) {
                    return null;
                }
            }
            for (int i = 1; file.exists(); i++) {
                file = new File(dir.getAbsolutePath() + File.separator + i + "_temp_" + fileName);
            }
            stuInfo.transferTo(file);
            redisTemplate.opsForValue().set(taskKey, 10, 5, TimeUnit.MINUTES);

            admService.readStuExcel(file, taskKey);

            jsonObject.put("taskKey", taskKey);
            jsonObject.put("msg", "成功上传, 数据库正在录入");
            jsonObject.put("status", true);
            return jsonObject;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            jsonObject.put("info", "student");
            redisTemplate.opsForValue().set(taskKey, -1, 5, TimeUnit.MINUTES);
            jsonObject.put("taskKey", taskKey);
            jsonObject.put("msg", e.getMessage());
            jsonObject.put("status", false);
            return jsonObject;
        }
    }

    /**
     * 获取上传进度
     *
     * @return 上传进度
     */
    @RequestMapping(value = "/file/stu-info", method = RequestMethod.GET)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public JSONObject getStuInfoUploadStatus(@RequestParam String taskKey) {
        return getUploadStatus(taskKey);
    }

    private JSONObject getUploadStatus(@RequestParam String taskKey) {
        JSONObject jsonObject = new JSONObject();
        Integer progress = (Integer) redisTemplate.opsForValue().get(taskKey);
        if (progress == null || progress == -1) {
            jsonObject.put("status", false);
            return jsonObject;
        }
        jsonObject.put("status", true);
        jsonObject.put("progress", progress);
        return jsonObject;
    }
}
