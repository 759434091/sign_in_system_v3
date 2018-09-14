package team.a9043.sign_in_system_version_2.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import team.a9043.sign_in_system_version_2.exception.InsufficientPermissionsException;
import team.a9043.sign_in_system_version_2.exception.TokenErrorException;
import team.a9043.sign_in_system_version_2.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StuInterceptor implements HandlerInterceptor {
    private RedisTemplate redisTemplate;

    @Autowired
    public StuInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //option
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
        if ((user.getUsrPermit() & 1) != 1) {
            throw new InsufficientPermissionsException("You are not an student!");
        }

        //return
        return true;
    }
}