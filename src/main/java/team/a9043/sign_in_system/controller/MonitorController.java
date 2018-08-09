package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.entity.SisSupervision;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.MonitorService;
import team.a9043.sign_in_system.util.judgetime.ScheduleParserException;

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

    @PostMapping("/courses/{scId}/monitor")
    @ApiOperation(value = "领取督导池", notes = "只可领取, 不可取消")
    public JSONObject modifyMonitor(@TokenUser @ApiIgnore SisUser sisUser,
                                    @PathVariable @ApiParam(value = "课程序号") String scId) throws
        InvalidPermissionException, IncorrectParameterException {

        return monitorService.modifyMonitor(sisUser, scId);
    }

    @GetMapping("/courses/{scId}/supervisions")
    @PreAuthorize("hasAuthority('MONITOR')")
    @ApiOperation(value = "获取督导记录", notes = "根据课程序号获取督导记录", httpMethod = "GET")
    public JSONObject getSupervisions(@TokenUser @ApiIgnore SisUser sisUser,
                                      @PathVariable @ApiParam(value = "课程序号") String scId) throws
        InvalidPermissionException, IncorrectParameterException {
        return monitorService.getSupervisions(sisUser, scId);
    }

    @PostMapping("/schedules/{ssId}/supervisions")
    @PreAuthorize("hasAuthority('MONITOR')")
    @ApiOperation(value = "插入督导记录", notes = "根据排课号插入督导记录", httpMethod = "POST"
        , produces = "application/json")
    public JSONObject insertSupervision(@TokenUser @ApiIgnore SisUser sisUser,
                                        @PathVariable
                                        @ApiParam(value = "排课号") Integer ssId,
                                        @RequestBody @Valid SisSupervision sisSupervision,
                                        @ApiIgnore BindingResult bindingResult) throws
        IncorrectParameterException,
        ScheduleParserException,
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
