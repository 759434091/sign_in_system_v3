package team.a9043.sign_in_system.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SisAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String jsonStr =
            String.format("{\"success\":false,\"error\":true,\"message\":\"Incorrect username or password: %s, %s\"}",
                request.getParameter("suId"), request.getParameter("suPassword"));
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(jsonStr);
    }
}
