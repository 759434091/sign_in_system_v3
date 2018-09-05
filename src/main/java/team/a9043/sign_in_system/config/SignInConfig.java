package team.a9043.sign_in_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Optional;

@Configuration
public class SignInConfig {
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Bean("sisRedisTemplate")
    public RedisTemplate<String, Object> sisRedisTemplate(RedisTemplate redisTemplate) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        Optional
            .ofNullable(redisTemplate.keys("*"))
            .ifPresent(redisTemplate::delete);
        return redisTemplate;
    }
}
