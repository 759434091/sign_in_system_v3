package team.a9043.mvcaop;

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
import team.a9043.pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Component
@Aspect
@Order()
public class TokenUserAspect {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger infoLog = LogManager.getLogger(TokenUserAspect.class);

    @Autowired
    public TokenUserAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around(value = "execution(* team.a9043.controller.*.*(.., @TokenUser (*), ..))", argNames = "pjp")
    public Object getUser(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Annotation[][] methodAnnotations = method.getParameterAnnotations();
        Object[] args = pjp.getArgs();
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        String token = request.getHeader("Access-Token");
        User user = null;
        Integer userArgs = null;
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
                        userArgs = i;
                        canBreak = true;
                        break;
                    }
                }
            }
        }
        if (userArgs != null && token != null && !token.trim().equals("")) {
            user = (User) redisTemplate.opsForValue().get("token_" + token);
            if (user != null) {
                args[userArgs] = user;
                return pjp.proceed(args);
            } else {
                if (!required) {
                    args[userArgs] = null;
                    infoLog.info("no user found");
                    return pjp.proceed(args);
                } else {
                    infoLog.info("no user found but required");
                    return null;
                }
            }
        } else {
            infoLog.error("parameter error");
            return null;
        }
    }
}
