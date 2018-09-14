package team.a9043.sign_in_system_version_2.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import team.a9043.sign_in_system_version_2.exception.InsufficientPermissionsException;
import team.a9043.sign_in_system_version_2.exception.TokenErrorException;
import team.a9043.sign_in_system_version_2.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdmInterceptor implements HandlerInterceptor {
    private RedisTemplate redisTemplate;

    @Autowired
    public AdmInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //options
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        //token
        String token = request.getHeader("Access-Token");
        if (token == null) {
            throw new TokenErrorException("Token not found!");
        }

        //user
        User user = (User) redisTemplate.opsForValue().get("token_" + token);
        if (user == null) {
            throw new TokenErrorException("No user found but required!");
        }

        //permit
        if ((user.getUsrPermit() & 8) != 8) {
            throw new InsufficientPermissionsException("You are not an administrator!");
        }

        //return
        return true;
    }
}
