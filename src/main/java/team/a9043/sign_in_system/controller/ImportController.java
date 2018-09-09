package team.a9043.sign_in_system.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.exception.UnknownServerError;
import team.a9043.sign_in_system.pojo.SisCourse;
import team.a9043.sign_in_system.pojo.SisDepartment;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisSchedule;
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
    @ApiOperation(value = "新增/修改课程", notes = "根据scId，if force, " +
        "将会删除旧课程、排课、教课、参课，再重新导入")
    public JSONObject createCourse(@PathVariable String scId,
                                   @RequestPart("course") String course,
                                   @RequestPart("scheduleList") String scheduleList,
                                   @RequestPart("departList") String departList) throws IncorrectParameterException, UnknownServerError {
        ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            SisCourse sisCourse =
                objectMapper.readValue(course, SisCourse.class);
            List<SisSchedule> sisScheduleList =
                objectMapper.readValue(scheduleList,
                    objectMapper.getTypeFactory().constructParametricType(List.class,
                        SisSchedule.class));
            List<SisDepartment> sisDepartmentList =
                objectMapper.readValue(departList,
                    objectMapper.getTypeFactory().constructParametricType(List.class,
                        SisDepartment.class));
            return importService.createCourse(scId, sisCourse,
                sisScheduleList, sisDepartmentList);
        } catch (IOException e) {
            throw new IncorrectParameterException(e.getMessage());
        }
    }

    @PutMapping("/courses/{scId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifyCourse(@PathVariable String scId,
                                   @RequestPart("course") String course,
                                   @RequestPart("mScheduleList") String mScheduleList,
                                   @RequestPart("nScheduleList") String nScheduleList,
                                   @RequestPart("departList") String departList) throws IncorrectParameterException, UnknownServerError {
        ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            SisCourse sisCourse =
                objectMapper.readValue(course, SisCourse.class);
            List<SisSchedule> mSisScheduleList =
                objectMapper.readValue(mScheduleList,
                    objectMapper.getTypeFactory().constructParametricType(List.class,
                        SisSchedule.class));
            List<SisSchedule> nSisScheduleList =
                objectMapper.readValue(nScheduleList,
                    objectMapper.getTypeFactory().constructParametricType(List.class,
                        SisSchedule.class));
            List<SisDepartment> sisDepartmentList =
                objectMapper.readValue(departList,
                    objectMapper.getTypeFactory().constructParametricType(List.class,
                        SisDepartment.class));
            return importService.modifyCourse(scId, sisCourse,
                mSisScheduleList, nSisScheduleList, sisDepartmentList);
        } catch (IOException e) {
            throw new IncorrectParameterException(e.getMessage());
        }
    }

    @DeleteMapping("/courses/{scId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject deleteCourse(@PathVariable String scId) throws IncorrectParameterException {
        return importService.deleteCourse(scId);
    }

    @PostMapping("/students/{suId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @ApiOperation(value = "新增/修改用户", notes = "根据suId，if " +
        "force，先删除旧用户信息（包括参加的课堂），然后再更新")
    public JSONObject createStudent(@PathVariable String suId,
                                    @RequestParam String suName,
                                    @RequestParam(value = "scIdList[]",
                                        required = false) List<String> scIdList,
                                    @RequestParam(value = "force", required =
                                        false) Boolean force) throws InvalidPermissionException, IncorrectParameterException {
        return importService.createStudent(suId, suName, scIdList, force);
    }

    @PutMapping("/students/{suId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifyStudent(@PathVariable String suId,
                                    @RequestParam String suName,
                                    @RequestParam(value = "scIdList[]",
                                        required = false) List<String> scIds) throws IncorrectParameterException {
        return importService.modifyStudent(suId, suName, scIds);
    }

    @PutMapping("/courses/{scId}/joinCourses")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifyJoinCourses(@PathVariable String scId,
                                        @RequestBody List<SisJoinCourse> joinCourseList) throws IncorrectParameterException, UnknownServerError {
        return importService.modifyJoinCourses(scId, joinCourseList);
    }

    @DeleteMapping("/joinCourses/{sjcId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject deleteJoinCourse(@PathVariable Integer sjcId) throws IncorrectParameterException {
        return importService.deleteJoinCourse(sjcId);
    }

    @PutMapping("/departments/{sdId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifyDepartment(@PathVariable Integer sdId,
                                       @RequestParam String sdName) throws IncorrectParameterException {
        return importService.modifyDepartment(sdId, sdName);
    }

    @DeleteMapping("/departments/{sdId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject deleteDepartment(@PathVariable Integer sdId) throws IncorrectParameterException {
        return importService.deleteDepartment(sdId);
    }

    @PostMapping("/departments")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject addDepartment(@RequestParam String sdName) throws IncorrectParameterException {
        return importService.addDepartment(sdName);
    }
}
