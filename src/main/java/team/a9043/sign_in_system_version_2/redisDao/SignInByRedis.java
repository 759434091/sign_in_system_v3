package team.a9043.sign_in_system_version_2.redisDao;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class SignInByRedis {
    private static final Logger infoLog = LoggerFactory.getLogger(SignInByRedis.class);
    private final RedisTemplate redisTemplate;

    @Autowired
    public SignInByRedis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean insertSignIn(String usrId, int siId, LocalDateTime siTime) {
        String key = "signIn_" + siId + "_" + usrId;
        if (isSignIn(key)) {
            return false;
        }
        Map<String, String> map = new HashMap<String, String>() {
            private static final long serialVersionUID = -5844106681400188942L;

            {
                this.put("userId", usrId);
                this.put("siId", String.valueOf(siId));
                this.put("siTime", String.valueOf(siTime));
            }
        };
        redisTemplate.opsForHash().putAll(key, map);
        infoLog.debug(key + "  已插入");
        infoLog.trace(JSONObject.valueToString(redisTemplate.opsForHash().entries(key)));
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean isSignIn(String key) {
        return redisTemplate.opsForHash().size(key) > 0;
    }
}
