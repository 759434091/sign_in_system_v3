package team.a9043.sign_in_system.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system.entity.SisSupervision;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.MonitorService;
import team.a9043.sign_in_system.util.judgetime.ScheduleParerException;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * @author a9043
 */
@RestController
public class MonitorController {
    @Resource
    private MonitorService monitorService;

    @GetMapping("/schedules/{ssId}/supervisions")
    @PreAuthorize("hasAuthority('MONITOR')")
    public JSONObject getSupervisions(@TokenUser SisUser sisUser,
                                      @PathVariable Integer ssId) throws
        InvalidPermissionException, IncorrectParameterException {
        return monitorService.getSupervisions(sisUser, ssId);
    }

    @PostMapping("/schedules/{ssId}/supervisions")
    @PreAuthorize("hasAuthority('MONITOR')")
    public JSONObject insertSupervision(@TokenUser SisUser sisUser,
                                        @PathVariable Integer ssId,
                                        @RequestBody @Valid SisSupervision sisSupervision,
                                        BindingResult bindingResult) throws
        IncorrectParameterException,
        ScheduleParerException,
        InvalidPermissionException {

        if (bindingResult.hasErrors()) {
            throw new IncorrectParameterException(new JSONArray(bindingResult.getAllErrors()).toString());
        }


        LocalDateTime localDateTime = LocalDateTime.now();
        return monitorService.insertSupervision(
            sisUser,
            ssId,
            sisSupervision,
            localDateTime);
    }
}
