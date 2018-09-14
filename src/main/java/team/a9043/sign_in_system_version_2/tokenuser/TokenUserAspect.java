package team.a9043.sign_in_system_version_2.tokenuser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import team.a9043.sign_in_system_version_2.exception.TokenErrorException;
import team.a9043.sign_in_system_version_2.pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@Aspect
@Order()
public class TokenUserAspect {
    private final RedisTemplate<Object, Object> redisTemplate;
    private static final Logger infoLog = LogManager.getLogger(TokenUserAspect.class);

    @Autowired
    public TokenUserAspect(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around(value = "execution(* team.a9043.sign_in_system_version_2.controller.*.*(.., @TokenUser (*), ..))", argNames = "pjp")
    public Object getUser(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Annotation[][] methodAnnotations = method.getParameterAnnotations();
        Object[] args = pjp.getArgs();
        Class[] argTypes = Arrays.stream(args).map(arg -> arg != null ? arg.getClass() : null).toArray(Class[]::new);
        String token = request.getHeader("Access-Token");
        User user;
        Integer userArgsIdx = null;
        boolean required = true;
        boolean canBreak = false;
        for (int i = 0; i < args.length; i++) {
            if (canBreak) {
                break;
            }
            if (argTypes[i].equals(User.class)) {
                for (Annotation annotation : methodAnnotations[i]) {
                    if (annotation.annotationType().equals(TokenUser.class)) {
                        required = ((TokenUser) annotation).required();
                        userArgsIdx = i;
                        canBreak = true;
                        break;
                    }
                }
            }
        }

        //token user
        if (userArgsIdx == null || token == null || token.trim().equals("")) {
            throw new TokenErrorException("Token " + token + " is an illegal token!");
        }

        user = (User) redisTemplate.opsForValue().get("token_" + token);
        if (user == null && required) {
            throw new TokenErrorException("No user found but required!");
        }
        args[userArgsIdx] = user;
        return pjp.proceed(args);
    }
}
