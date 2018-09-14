package team.a9043.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.a9043.mapper.AspectMapper;
import team.a9043.pojo.Schedule;
import team.a9043.pojo.SuvMan;
import team.a9043.pojo.SuvSch;

import java.util.List;

@Component
@Aspect
public class ServiceAOP {

    private AspectMapper aspectMapper;

    @Autowired
    public ServiceAOP(AspectMapper aspectMapper) {
        this.aspectMapper = aspectMapper;
    }

    @Around(value = "(" +
            "execution(* team.a9043.serviceImpl.AdminServiceImpl.setCozSignIn(..))||" +
            "execution(* team.a9043.serviceImpl.TchServiceImpl.setCozSignIn(..))||" +
            "execution(* team.a9043.serviceImpl.SuvServiceImpl.openAutoSignIn(..)) ||" +
            "execution(* team.a9043.serviceImpl.SuvServiceImpl.openManSignIn(..)) ||" +
            "execution(* team.a9043.serviceImpl.SuvServiceImpl.initManSignIn(..)) ||" +
            "execution(* team.a9043.serviceImpl.SuvServiceImpl.initAutoSignIn(..))" +
            ")" +
            "&& args(..,suvMan)",
            argNames = "pjp,suvMan")
    public boolean AroundSignIn(ProceedingJoinPoint pjp, SuvMan suvMan) throws Throwable {
        Schedule thisSchedule = aspectMapper.findSchCoz(suvMan.getSchId());
        return checkSchedule(pjp, thisSchedule);
    }

    @Around(value = "(" +
            "execution(* team.a9043.serviceImpl.AdminServiceImpl.setCozSuv(..)) ||" +
            "execution(* team.a9043.serviceImpl.TchServiceImpl.setCozSuv(..))" +
            ")" +
            "&&args(..,suvSch)", argNames = "pjp ,suvSch")
    public boolean AroundSuvSch(ProceedingJoinPoint pjp, SuvSch suvSch) throws Throwable {
        Schedule thisSchedule = aspectMapper.findSchCoz(suvSch.getSchedule().getSchId());
        return checkSchedule(pjp, thisSchedule);
    }

    private boolean checkSchedule(ProceedingJoinPoint pjp, Schedule thisSchedule) throws Throwable {
        int siTime = thisSchedule.getSchTime();
        if (siTime == 1 || siTime == 5 || siTime == 9) {
            return (boolean) pjp.proceed();
        } else {
            List<Schedule> scheduleList = aspectMapper.findCozSch(thisSchedule.getCozId());
            for (Schedule tempSchedule : scheduleList) {
                if (tempSchedule.getSchYear() == thisSchedule.getSchYear() &&
                        tempSchedule.isSchTerm() == thisSchedule.isSchTerm() &&
                        tempSchedule.getSchWeek() == thisSchedule.getSchWeek() &&
                        tempSchedule.getSchFortnight() == thisSchedule.getSchFortnight() &&
                        tempSchedule.getSchDay() == thisSchedule.getSchDay() &&
                        (thisSchedule.getSchTime() - tempSchedule.getSchTime() == 1)) {
                    return false;
                }
            }
        }
        return (boolean) pjp.proceed();
    }
}
