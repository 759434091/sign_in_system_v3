package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.entity.SisCourse;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.CourseService;
import team.a9043.sign_in_system.service.MonitorService;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * @author a9043
 */
@RestController
public class CourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private MonitorService monitorService;

    @GetMapping("/week")
    public JSONObject getWeek() throws InvalidTimeParameterException {
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server_time", localDateTime);
        jsonObject.put("week",
            JudgeTimeUtil.getWeek(localDateTime.toLocalDate()));
        return jsonObject;
    }

    /**
     * 获得课程
     *
     * @param sisUser     用户
     * @param needMonitor null: 忽略
     *                    true: 开启督导课程
     *                    false: 关闭督导课程
     * @param hasMonitor  null: 忽略
     *                    true： 已有督导员
     *                    false： 无督导员
     * @param page        administrator 分页页数
     * @param getType     API 类型 （student, monitor, administrator）
     * @return json
     * @throws IncorrectParameterException 参数非法
     * @throws InvalidPermissionException  权限非法
     */
    @GetMapping("/courses")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','STUDENT','MONITOR')")
    @ApiOperation(value = "获得课程",
        notes = "根据getType获取课程",
        produces = "application/json")
    public JSONObject getCourses(@TokenUser @ApiIgnore SisUser sisUser,
                                 @RequestParam(required = false) @ApiParam(value = "是否需要督导filter,若该参数为null则忽略hasMonitor") Boolean needMonitor,
                                 @RequestParam(required = false) @ApiParam(value = "是否已有督导员filter") Boolean hasMonitor,
                                 @RequestParam @ApiParam(value = "分页filter") Integer page,
                                 @RequestParam
                                 @ApiParam(value = "获得方式",
                                     allowableValues = "student,monitor," +
                                         "administrator")
                                     String getType) throws
        IncorrectParameterException, InvalidPermissionException, ExecutionException, InterruptedException {

        switch (getType) {
            case "administrator": {
                if (!sisUser.getSuAuthoritiesStr().contains("ADMINISTRATOR")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + getType);
                }
                return courseService.getCourses(needMonitor, hasMonitor, page);
            }
            case "monitor": {
                if (!sisUser.getSuAuthoritiesStr().contains("MONITOR")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + getType);
                }
                if (needMonitor && !hasMonitor) {
                    return courseService.getCourses(true, false, page);
                }
                return monitorService.getCourses(sisUser);
            }
            case "student": {
                if (!sisUser.getSuAuthoritiesStr().contains("STUDENT")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + getType);
                }
                return courseService.getCourses(sisUser);
            }
            default:
                throw new IncorrectParameterException("Incorrect " +
                    "getType:" + getType);
        }
    }

    /**
     * 修改是否需要督导
     *
     * @param sisCourse     课程json
     * @param scId          课程id
     * @param bindingResult 检验结果
     * @return json
     * @throws IncorrectParameterException 参数非法
     */
    @PutMapping("/courses/{scId}/sc-need-monitor")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @ApiOperation(value = "修改督导",
        notes = "根据scId修改督导,SisCourse -> {\n  scNeedMonitor: boolean,\n  " +
            "monitor: SisUser - {suId: String}\n}",
        produces = "application/json")
    public JSONObject modifyScNeedMonitor(@RequestBody @Valid SisCourse sisCourse,
                                          @PathVariable
                                          @ApiParam(value = "课程序号") String scId,
                                          @ApiIgnore BindingResult bindingResult) throws IncorrectParameterException {
        if (bindingResult.hasErrors()) {
            throw new IncorrectParameterException(new JSONArray(bindingResult.getAllErrors()).toString());
        }
        if (!scId.equals(sisCourse.getScId())) {
            throw new IncorrectParameterException("Incorrect scId");
        }
        return courseService.modifyScNeedMonitor(sisCourse);
    }
}
