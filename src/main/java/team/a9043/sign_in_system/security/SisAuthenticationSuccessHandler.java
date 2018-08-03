package team.a9043.sign_in_system.security;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.entity.SecurityUserDetails;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SisAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) authentication.getPrincipal();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userName", securityUserDetails.getUsername());

        String token = JwtUtil.createJWT(claimsMap);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access-token", token);
        response.getWriter().write(jsonObject.toString());
    }
}
