package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author a9043
 */
@RestController
public class TestController {
    @GetMapping(value = "/resources")
    @PreAuthorize("hasAuthority('STUDENT')")
    public JSONObject getR() {
        return new JSONObject(SecurityContextHolder.getContext().getAuthentication());
    }
}
