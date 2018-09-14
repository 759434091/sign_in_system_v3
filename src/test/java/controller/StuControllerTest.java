package controller;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class StuControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testSignIn() {
        //doLogin
        String token = UUID.randomUUID().toString().replace("-", "");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("usrId", "2016220401001");
        jsonObject.put("usrPwd", "123456");
        HttpHeaders headersMap = new HttpHeaders();
        headersMap.add("Access-Token", token);
        headersMap.add("Content-Type", "application/json;charset=utf-8");
        redisTemplate.opsForValue().set("token_" + token, "success");
        redisTemplate.expire("token_" + token, 60 * 10, TimeUnit.SECONDS);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headersMap);
        String result = testRestTemplate.postForObject("/user", httpEntity, String.class);
        System.out.println("\033[36m" + result + "\033[0m");
        //doSignIn
        headersMap.clear();
        headersMap.add("Access-Token", token);
        headersMap.add("Content-Type", "application/json;charset=utf-8");
        JSONObject location = new JSONObject();
        location.put("locLat", "30.6746773009");
        location.put("locLong", "104.1028136015");
        jsonObject = new JSONObject();
        jsonObject.put("schId", 1);
        jsonObject.put("schSignInWeek", 8);
        jsonObject.put("location", location);
        httpEntity = new HttpEntity<>(jsonObject.toString(), headersMap);
        result = testRestTemplate.postForObject("/student/signIn", httpEntity, String.class);
        System.out.println("\033[36m" + result + "\033[0m");
        redisTemplate.delete("token_" + token);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSignInAndAbs() {
        //doLogin
        String token = UUID.randomUUID().toString().replace("-", "");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("usrId", "2016220401001");
        jsonObject.put("usrPwd", "123456");
        HttpHeaders headersMap = new HttpHeaders();
        headersMap.add("Access-Token", token);
        headersMap.add("Content-Type", "application/json;charset=utf-8");
        redisTemplate.opsForValue().set("token_" + token, "success");
        redisTemplate.expire("token_" + token, 60 * 10, TimeUnit.SECONDS);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headersMap);
        String result = testRestTemplate.postForObject("/user/user", httpEntity, String.class);
        System.out.println("\033[36m" + result + "\033[0m");
        //doGet
        headersMap.add("Access-Token", token);
        httpEntity = new HttpEntity<>("", headersMap);
        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/student/history?" + "cozId=E0911835.02", HttpMethod.GET, httpEntity, String.class);
        System.out.println("\033[36m" + responseEntity.toString() + "\033[0m");
        redisTemplate.delete("token_" + token);
    }
}
