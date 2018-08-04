package team.a9043.sign_in_system.security;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.util.JwtUtil;

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
                                        Authentication authentication) throws IOException {
        SecurityUserDetails securityUserDetails =
            (SecurityUserDetails) authentication.getPrincipal();
        SisUser sisUser = securityUserDetails.getSisUser();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("suId", sisUser.getSuId());
        claimsMap.put("suName", sisUser.getSuName());
        claimsMap.put("suAuthoritiesStr", sisUser.getSuAuthoritiesStr());

        String token = JwtUtil.createJWT(claimsMap);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access-token", token);
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(jsonObject.toString());
    }
}
