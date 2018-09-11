package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.convertor.JsonObjectHttpMessageConverter;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisJoinCourseExample;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ConcurrentTest {
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    private RestTemplate restTemplate;

    public ConcurrentTest() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
            .build();
    }

    @Test
    public void test() {
        String scId = "B1701920.16";
        int ssId = 426;
        SisJoinCourseExample sisJoinCourseExample = new
            SisJoinCourseExample();
        sisJoinCourseExample.createCriteria()
            .andScIdEqualTo(scId)
            .andJoinCourseTypeEqualTo(SisJoinCourse.JoinCourseType.ATTENDANCE
                .ordinal());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        sisJoinCourseList
            .parallelStream()
            .map(sjc -> {
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("suId", sjc.getSuId());
                claimsMap.put("suName", "");
                claimsMap.put("suAuthoritiesStr", "STUDENT");
                claimsMap.put("type", "code");
                return JwtUtil.createJWT(claimsMap);
            })
            .map(token -> {
                HttpHeaders httpHeaders = new HttpHeaders();
                try {
                    httpHeaders.add("Access-Token", getAccessToken());
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                httpHeaders.add("Authorization",
                    "Bearer " + token);
                return httpHeaders;
            })
            .forEach(headers -> {
                try {
                    String jsonObject1 =
                        restTemplate.postForObject(String.format(
                            "https://api.xsix103.cn/sign_in_system/v3" +
                                "/schedules/%d/signIns/doSignIn", ssId),
                            new HttpEntity<String>(headers),
                            String.class);
                    log.info(jsonObject1);
                } catch (HttpClientErrorException e) {
                    log.error(new String(e.getResponseBodyAsByteArray()));
                }
            });

    }

    public String getAccessToken() throws NoSuchPaddingException,
        NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
        IllegalBlockSizeException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loc_lat", (double) 1);
        jsonObject.put("loc_long", (double) 1);

        byte[] bytes = Base64.getDecoder()
            .decode("JWmPJIqFj+Lxu4GbO/RP7w==");
        SecretKeySpec secretKeySpec =
            new SecretKeySpec(bytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        return Base64.getEncoder().encodeToString(cipher.doFinal(
            jsonObject.toString().getBytes()));
    }
}
