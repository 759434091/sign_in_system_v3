package mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.redisDao.SignInByRedis;

import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class SignInByRedisTest {

    @Autowired
    SignInByRedis signInByRedis;
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void insertSignIn() {
        String userId = "2016220401001";
        int siId = 1;
        LocalDateTime siTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        String key = "signIn_" + siId + "_" + userId;
        Boolean res = signInByRedis.insertSignIn(userId, siId, siTime);
        Boolean res2 = signInByRedis.insertSignIn(userId, siId, siTime);
        System.out.println("测试第一次插入" + res + "       测试重复插入" + res2 + "       测试删除" + redisTemplate.delete(key));
    }

    @Test
    public void isSignIn() {
        Boolean isSignIn = signInByRedis.isSignIn("123456");
        System.out.println(isSignIn);
    }
}
