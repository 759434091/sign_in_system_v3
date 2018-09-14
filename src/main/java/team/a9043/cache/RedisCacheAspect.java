package team.a9043.cache;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.a9043.mapper.SignInMapper;
import team.a9043.pojo.Schedule;
import team.a9043.pojo.SuvMan;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Aspect
public class RedisCacheAspect {
    private static final Logger infoLog = LogManager.getLogger(RedisCacheAspect.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final SignInMapper signInMapper;

    @Autowired
    public RedisCacheAspect(RedisTemplate<String, Object> redisTemplate, SignInMapper signInMapper) {
        this.redisTemplate = redisTemplate;
        this.signInMapper = signInMapper;
    }

    @AfterReturning(value = "execution(* team.a9043.cache.CacheProcess.insertSignIn(..))", returning = "resFake")
    public void insertSignInDataBase(String resFake) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(resFake);
        boolean res = map != null &&
                map.get("userId") != null &&
                map.get("schId") != null &&
                map.get("siTime") != null &&
                map.get("siWeek") != null &&
                signInMapper.insertSignIn(
                        map.get("userId").toString(),
                        Integer.valueOf(map.get("schId").toString()),
                        LocalDateTime.parse(map.get("siTime").toString()),
                        Integer.valueOf(map.get("siWeek").toString()));
        if (res) {
            redisTemplate.delete(resFake);
            infoLog.debug(resFake + " 已插入数据库");
        } else {
            infoLog.error(resFake + " 插入数据库失败");
        }
    }

    @Around(value = "execution(* team.a9043.mapper.SignInMapper.findSchedule(..))&&args(schId)", argNames = "jp,schId")
    public Object findScheduleCache(ProceedingJoinPoint jp, int schId) throws Throwable {
        Schedule schedule = (Schedule) redisTemplate.opsForValue().get("schedule_" + schId);
        if (schedule == null) {
            infoLog.debug("key: schedule_" + schId + "  未命中");
            return jp.proceed();
        } else {
            infoLog.debug("key: schedule_" + schId + "  命中");
            infoLog.trace(JSON.toJSONString(schedule));
            return schedule;
        }
    }

    //@Around(value = "execution(* team.a9043.mapper.SignInMapper.findSuvMan(..))&&args(schId,siWeek)", argNames = "jp,schId,siWeek")
    public Object findSuvManCache(ProceedingJoinPoint jp, int schId, int siWeek) throws Throwable {
        SuvMan suvMan = (SuvMan) redisTemplate.opsForValue().get("suvMan_" + schId + "_" + siWeek);
        if (suvMan == null) {
            infoLog.debug("suvMan_" + schId + "_" + siWeek + "  未命中");
            return jp.proceed();
        } else {
            infoLog.debug("suvMan_" + schId + "_" + siWeek + "  命中");
            infoLog.trace(JSON.toJSONString(suvMan));
            return suvMan;
        }
    }
}
