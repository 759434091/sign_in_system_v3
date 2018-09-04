package team.a9043.sign_in_system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;
import team.a9043.sign_in_system.service.SignInService;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author a9043
 */
@RestController
public class SignInController {
    @Resource
    private SignInService signInService;

    @GetMapping("/courses/{scId}/signIns")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','STUDENT','TEACHER')")
    @ApiOperation("获得签到以及历史")
    public JSONObject getSignIns(@TokenUser @ApiIgnore SisUser sisUser,
                                 @PathVariable @ApiParam("课程") String scId,
                                 @RequestParam
                                 @ApiParam(value = "查询类型", allowableValues =
                                     "student, teacher, administrator")
                                     String queryType) throws IncorrectParameterException, InvalidPermissionException {
        switch (queryType) {
            case "teacher":
                if (!sisUser.getSuAuthoritiesStr().contains("TEACHER")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + queryType);
                }
                return signInService.getSignIns(scId);
            case "administrator":
                if (!sisUser.getSuAuthoritiesStr().contains("ADMINISTRATOR")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + queryType);
                }
                return signInService.getSignIns(scId);
            case "student":
                if (!sisUser.getSuAuthoritiesStr().contains("STUDENT")) {
                    throw new InvalidPermissionException(
                        "Invalid permission:" + queryType);
                }
                return signInService.getSignIns(sisUser, scId);
            default:
                throw new IncorrectParameterException(
                    "Incorrect queryType: " + queryType);
        }
    }

    @PostMapping("/schedules/{ssId}/signIns")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','TEACHER')")
    @ApiOperation("发起签到")
    public JSONObject createSignIn(@TokenUser @ApiIgnore SisUser sisUser,
                                   @PathVariable @ApiParam("排课") Integer ssId) throws InvalidTimeParameterException, InvalidPermissionException {
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            signInService.createSignIn(sisUser, ssId, localDateTime));
        return jsonObject;
    }

    @GetMapping("/schedules/{ssId}/signIns/week/{week}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','TEACHER')")
    @ApiOperation("获得签到")
    public JSONObject getSignIn(@PathVariable @ApiParam("排课") Integer ssId,
                                @PathVariable @ApiParam("签到周") Integer week) {
        return signInService.getSignIn(ssId, week);
    }

    @PostMapping("/schedules/{ssId}/signIns/doSignIn")
    @PreAuthorize("hasAnyAuthority('STUDENT') && authentication.sisUser.type" +
        ".equals('code')")
    @ApiOperation("学生签到")
    public JSONObject signIn(@TokenUser @ApiIgnore SisUser sisUser,
                             @PathVariable @ApiParam("排课") Integer ssId,
                             @RequestHeader("location") String location) throws IncorrectParameterException, InvalidTimeParameterException {
        JSONObject locationJson;
        try {
            locationJson = new JSONObject(location);
        } catch (JSONException e) {
            throw new IncorrectParameterException(e.getMessage());
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            signInService.signIn(sisUser, ssId, localDateTime, locationJson));
        return jsonObject;
    }
}
