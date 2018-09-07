package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.pojo.SisSchedule;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.service.FileService;
import team.a9043.sign_in_system.service.ImportService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author: 卢学能 zzz13129180808@gmail.com
 * @date: 2018/9/6 下午7:33
 */
@RestController
public class ImportController {
    @Resource
    private ImportService importService;
    @Resource
    private FileService fileService;

    @GetMapping("/progress/{key}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject getProgress(@PathVariable String key) {
        return importService.getProgress(key);
    }

    @PostMapping("/courses/import")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject importCozInfo(@RequestPart("cozInfo") MultipartFile multipartFile) throws IOException, InvalidFormatException {
        return fileService.readCozInfo(multipartFile);
    }

    @PostMapping("/students/import")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject importStuInfo(@RequestPart("stuInfo") MultipartFile multipartFile) throws IOException, InvalidFormatException {
        return fileService.readStuInfo(multipartFile);
    }

    @PostMapping("/courses/{scId}")
    @ApiOperation(value = "新增/修改课程", notes = "根据scId，if force, 将会删除旧课程、排课、教课、参课，再重新导入")
    public JSONObject createCourse(@PathVariable String scId,
                                   @RequestParam("scName") String scName,
                                   @RequestParam(value = "scGrade", required = false) String scGrade,
                                   @RequestParam(value = "sdNameList[]", required = false) String[] sdNameList,
                                   @RequestParam(value = "sdIdLis[]t", required = false) Integer[] sdIdList,
                                   @RequestParam(value = "teacherList[]", required = false) List<SisUser> teacherList,
                                   @RequestParam(value = "force", required = false) Boolean force) {
        return null;//todo
    }

    @PutMapping("/courses/{scId}")
    public JSONObject modifyCourse(@PathVariable String scId,
                                   @RequestParam("scName") String scName,
                                   @RequestParam(value = "scGrade", required = false) String scGrade,
                                   @RequestParam(value = "sdNameList[]", required = false) String[] sdNameList,
                                   @RequestParam(value = "sdIdLis[]t", required = false) Integer[] sdIdList,
                                   @RequestParam(value = "teacherList[]", required = false) List<SisUser> teacherList) {
        return null;//todo
    }

    @DeleteMapping("/courses/{scId}")
    public JSONObject deleteCourse(@PathVariable String scId) throws IncorrectParameterException {
        return importService.deleteCourse(scId);
    }

    @PostMapping("/courses/{scId}/schedules")
    @ApiOperation(value = "新增/修改排课", notes = "根据scId，if force, 先删除旧排课（保留已存在地点），然后重新更新")
    public JSONObject createSchedule(@PathVariable String scId,
                                     @RequestBody SisSchedule sisSchedule,
                                     @RequestParam(value = "slName", required = false) String slName,
                                     @RequestParam(value = "slLat", required = false) Double slLat,
                                     @RequestParam(value = "slLong", required = false) Double slLong,
                                     @RequestParam(value = "force", required = false) Boolean force) {
        return null;//todo
    }

    @PutMapping("/schedules/{ssId}")
    public JSONObject modifySchedule(@PathVariable String ssId,
                                     @RequestBody SisSchedule sisSchedule,
                                     @RequestParam(value = "slName", required = false) String slName,
                                     @RequestParam(value = "slLat", required = false) Double slLat,
                                     @RequestParam(value = "slLong", required = false) Double slLong,
                                     @RequestParam(value = "force", required = false) Boolean force) {
        return null;//todo
    }

    @DeleteMapping("/schedules/{ssId}")
    public JSONObject deleteSchedule(@PathVariable Integer ssId) {
        return null;//todo
    }

    @PostMapping("/students/{suId}")
    @ApiOperation(value = "新增/修改用户", notes = "根据suId，if force，先删除旧用户信息（包括参加的课堂），然后再更新")
    public JSONObject createStudent(@PathVariable String suId,
                                    @RequestParam String suName,
                                    @RequestParam(value = "scIdList[]", required = false) String[] scIdList,
                                    @RequestParam(value = "force", required = false) Boolean force) {
        return null;//todo
    }

    @PutMapping("/students/{suId}")
    public JSONObject createStudent(@PathVariable String suId,
                                    @RequestParam String suName,
                                    @RequestParam(value = "scIdList[]", required = false) String[] scIdList) {
        return null;//todo
    }

    @DeleteMapping("/joinCourses/{sjcId}")
    public JSONObject deleteJoinCourse(@PathVariable Integer sjcId) {
        return null;//todo
    }
}
