package team.a9043.sign_in_system.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.CourseService;

import javax.annotation.Resource;

/**
 * @author a9043
 */
@RestController
public class CourseController {
    @Resource
    private CourseService courseService;

    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject getCourses(@RequestParam(required = false) Integer page) {
        return courseService.getCourses(page);
    }

    @GetMapping("/courses/table")
    @PreAuthorize("hasAuthority('STUDENT')")
    public JSONObject getCourses(@TokenUser SisUser sisUser) {
        return courseService.getCourses(sisUser);
    }

    @PostMapping("/schedules/{ssId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject setSsNeedMonitor(@RequestBody SisSchedule sisSchedule,
                                       @PathVariable Integer ssId,
                                       BindingResult bindingResult) throws IncorrectParameterException {
        if (bindingResult.hasErrors()) {
            throw new IncorrectParameterException(new JSONArray(bindingResult.getAllErrors()).toString());
        }
        if (!ssId.equals(sisSchedule.getSsId())) {
            throw new IncorrectParameterException("Incorrect ssId");
        }
        return courseService.setSsNeedMonitor(sisSchedule);
    }
}
