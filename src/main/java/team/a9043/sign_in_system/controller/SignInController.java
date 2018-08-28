package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
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
    public JSONObject getSignIns(@TokenUser @ApiIgnore SisUser sisUser,
                                 @PathVariable String scId,
                                 @RequestParam String queryType) throws IncorrectParameterException {
        switch (queryType) {
            case "teacher":
                return signInService.getSignIns(scId);
            case "administrator":
                return signInService.getSignIns(scId);
            case "student":
                return signInService.getSignIns(sisUser, scId);
            default:
                throw new IncorrectParameterException(
                    "Incorrect queryType: " + queryType);
        }
    }

    @PostMapping("/schedules/{ssId}/signIns")
    public JSONObject createSignIn(@TokenUser @ApiIgnore SisUser sisUser,
                                   @PathVariable Integer ssId) throws InvalidTimeParameterException, InvalidPermissionException {
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            signInService.createSignIn(sisUser, ssId, localDateTime));
        return jsonObject;
    }

    @GetMapping("/schedules/{ssId}/signIns/week/{week}")
    public JSONObject getSignIn(@PathVariable Integer ssId,
                                @PathVariable Integer week) {
        return signInService.getSignIn(ssId, week);
    }

    @PostMapping("/schedules/{ssId}/signIns/doSignIn")
    public JSONObject signIn(@TokenUser @ApiIgnore SisUser sisUser,
                             @PathVariable Integer ssId) throws IncorrectParameterException, InvalidTimeParameterException {
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            signInService.signIn(sisUser, ssId, localDateTime));
        return jsonObject;
    }
}
