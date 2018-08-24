package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
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

    @PostMapping("/schedules/{ssId}/signIns")
    public JSONObject createSignIn(@PathVariable Integer ssId) throws InvalidTimeParameterException {
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",
            signInService.createSignIn(ssId, localDateTime));
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
