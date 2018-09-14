package team.a9043.cache;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CacheProcessImpl implements CacheProcess{
    private static final Logger infoLog = LogManager.getLogger(CacheProcessImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheProcessImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String insertSignIn(String userId, int schId, LocalDateTime siTime, int siWeek) {
        String key = "signIn_" + userId + "_" + schId + "_" + siWeek;
        Map<String, String> map = new HashMap<String, String>() {
            private static final long serialVersionUID = -5844106681400188942L;

            {
                this.put("userId", userId);
                this.put("schId", String.valueOf(schId));
                this.put("siTime", String.valueOf(siTime)); 
                this.put("siWeek", String.valueOf(siWeek));

            }
        };
        redisTemplate.opsForHash().putAll(key, map);
        infoLog.debug(key + "  已插入");
        infoLog.trace(JSON.toJSONString(redisTemplate.opsForHash().entries(key)));
        return key;
    }
}
