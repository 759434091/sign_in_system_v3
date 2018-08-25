package team.a9043.sign_in_system.security.handler;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.security.entity.SecurityUserDetails;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SisAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @SuppressWarnings("ConstantConditions")
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


        sisUser.setSisMonitorTrans(null);
        sisUser.setSisSignInDetails(null);
        sisUser.setSisCourses(null);
        sisUser.setSisJoinCourses(null);
        sisUser.setSuPassword(null);

        response.setHeader("Content-type", "application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("error", false);
        jsonObject.put("access_token", token);
        jsonObject.put("user", new JSONObject(sisUser));
        response.getWriter().write(jsonObject.toString());
    }
}
