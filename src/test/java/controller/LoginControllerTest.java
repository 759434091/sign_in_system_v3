package controller;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class LoginControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void doLogin() {
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
        redisTemplate.delete("token_" + token);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void checkLogin() {
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
        //doCheck
        headersMap.clear();
        headersMap.add("Access-Token", token);
        httpEntity = new HttpEntity<>("", headersMap);
        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/user/user", HttpMethod.GET, httpEntity, String.class);
        System.out.println("\033[36m" + responseEntity.toString() + "\033[0m");
        redisTemplate.delete("token_" + token);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void changePassword() {
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
        //doChange
        headersMap.clear();
        headersMap.add("Access-Token", token);
        headersMap.add("Content-Type", "application/json;charset=utf-8");
        jsonObject = new JSONObject();
        jsonObject.put("old_pwd", "123456");
        jsonObject.put("new_pwd", "123456");
        httpEntity = new HttpEntity<>(jsonObject.toString(), headersMap);
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);
        result = restTemplate.patchForObject("http://localhost:" + port + "/user/user", httpEntity, String.class);
        System.out.println("\033[36m" + result + "\033[0m");
        redisTemplate.delete("token_" + token);
    }

    @Test
    public void getWeek() {
        String result = testRestTemplate.getForObject("/user/week", String.class);
        System.out.println("\033[36m" + result + "\033[0m");
    }
}