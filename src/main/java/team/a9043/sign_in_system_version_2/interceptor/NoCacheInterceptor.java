package team.a9043.sign_in_system_version_2.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class NoCacheInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        response.setDateHeader("Expires", -1);
        response.setHeader("Cache_Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }
}
