package team.a9043.sign_in_system.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.CourseService;
import team.a9043.sign_in_system.service.MonitorService;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author a9043
 */
@RestController
public class CourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private MonitorService monitorService;

    @GetMapping("/courses")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','STUDENT','MONITOR')")
    public JSONObject getCourses(@TokenUser SisUser sisUser,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam String getType) throws
        IncorrectParameterException, InvalidPermissionException {

        switch (getType) {
            case "administrator": {
                if (!sisUser.getSuAuthoritiesStr().contains("ADMINISTRATOR")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + getType);
                }
                return courseService.getCourses(page);
            }
            case "monitor": {
                if (!sisUser.getSuAuthoritiesStr().contains("MONITOR")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + getType);
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

    @PostMapping("/schedules/{ssId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifySsNeedMonitor(@RequestBody @Valid SisSchedule sisSchedule,
                                          @PathVariable Integer ssId,
                                          BindingResult bindingResult) throws IncorrectParameterException {
        if (bindingResult.hasErrors()) {
            throw new IncorrectParameterException(new JSONArray(bindingResult.getAllErrors()).toString());
        }
        if (!ssId.equals(sisSchedule.getSsId())) {
            throw new IncorrectParameterException("Incorrect ssId");
        }
        return courseService.modifySsNeedMonitor(sisSchedule);
    }
}
