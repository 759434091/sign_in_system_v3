package team.a9043.sign_in_system_version_2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
@Aspect
public class TimeFreezeAspect {

    @Around(value = "execution(* team.a9043.sign_in_system_version_2.service.*.*(..,java.time.LocalDateTime,..)))")
    public Object freezeDateTime(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = pjp.getArgs();
        Class[] argTypes = new Class[args.length];
        IntStream
                .range(0, args.length)
                .forEach(i -> argTypes[i] = (args[i] != null) ? args[i].getClass() : Object.class);
        IntStream
                .range(0, args.length)
                .filter(i -> (argTypes[i].equals(LocalDateTime.class) && parameterNames[i].equals("currentDateTime")))
                .forEach(i -> args[i] = LocalDateTime.of(2018, 4, 23, 14, 35, 0));
        //冻结 第七周 星期一 下午56节课 计网
        return pjp.proceed(args);
    }
}
