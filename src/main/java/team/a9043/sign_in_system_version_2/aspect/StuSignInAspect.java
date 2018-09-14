package team.a9043.sign_in_system_version_2.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.mapper.CourseMapper;
import team.a9043.sign_in_system_version_2.util.TransSchedule;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class StuSignInAspect {
    private final Logger logger = LoggerFactory.getLogger(StuSignInAspect.class);
    private final RedisTemplate redisTemplate;
    private final TransSchedule transSchedule;
    @Resource
    private CourseMapper courseMapper;

    @Autowired
    public StuSignInAspect(RedisTemplate redisTemplate, TransSchedule transSchedule) {
        this.redisTemplate = redisTemplate;
        this.transSchedule = transSchedule;
    }

    @Transactional
    @SuppressWarnings({"unchecked", "rawtypes"})
    @AfterReturning(value = "execution(* team.a9043.sign_in_system_version_2.redisDao.SignInByRedis.insertSignIn(..)) &&" +
            "args(usrId,siId,siTime)", argNames = "usrId,siId,siTime,retVal", returning = "retVal")
    public void insertSignInDataBase(String usrId, int siId, LocalDateTime siTime, Boolean retVal) {
        if (retVal) {
            String key = "signIn_" + siId + "_" + usrId;
            boolean finalRes = courseMapper.signInInDatabase(
                    transSchedule.getYear(siTime),
                    transSchedule.getTerm(siTime),
                    siTime,
                    siId,
                    usrId);
            if (finalRes) {
                redisTemplate.expire(key, 40, TimeUnit.MINUTES);
                logger.debug(key + " 已插入数据库");
            } else {
                logger.error(key + siTime + "       插入数据库失败");
            }
        }
    }
}
