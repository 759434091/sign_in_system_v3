package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.security.tokenuser.TokenUser;

/**
 * @author a9043
 */
@RestController
public class TestController {
    @GetMapping(value = "/resources")
    @PreAuthorize("hasAuthority('STUDENT')")
    public JSONObject getR(@TokenUser SisUser sisUser) {
        return new JSONObject(sisUser);
    }
}
