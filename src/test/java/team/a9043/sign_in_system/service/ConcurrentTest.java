package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.pojo.*;
import team.a9043.sign_in_system.util.JwtUtil;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ConcurrentTest {
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private SignInService signInService;
    private RestTemplate restTemplate;

    public ConcurrentTest() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
            .build();
    }

    @Test
    public void test() {
        List<String> scIdList = Arrays.asList(
            "E0911835.01", "E0911835.02", "E0911835.03", "E0911835.04",
            "E0911835.05", "E0911835.06", "E0911835.07"
        );
        SisScheduleExample sisScheduleExample = new SisScheduleExample();
        sisScheduleExample.createCriteria().andScIdIn(scIdList);
        List<SisSchedule> sisScheduleList =
            sisScheduleMapper.selectByExample(sisScheduleExample);

        LocalDateTime localDateTime = LocalDateTime.now();
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401000");
        sisUser.setSuAuthoritiesStr("ADMINISTRATOR");
        sisScheduleList.parallelStream()
            .forEach(s -> {
                try {
                    signInService.createSignIn(sisUser, s.getSsId(),
                        localDateTime);
                } catch (InvalidTimeParameterException |
                InvalidPermissionException e) {
                    e.printStackTrace();
                }
            });

        SisJoinCourseExample sisJoinCourseExample = new
            SisJoinCourseExample();
        sisJoinCourseExample.createCriteria()
            .andScIdIn(scIdList)
            .andJoinCourseTypeEqualTo(SisJoinCourse.JoinCourseType.ATTENDANCE
                .ordinal());
        List<SisJoinCourse> sisJoinCourseList =
            sisJoinCourseMapper.selectByExample(sisJoinCourseExample);

        sisJoinCourseList
            .parallelStream()
            .forEach(sjc -> {
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("suId", sjc.getSuId());
                claimsMap.put("suName", "");
                claimsMap.put("suAuthoritiesStr", "STUDENT");
                claimsMap.put("type", "code");

                HttpHeaders httpHeaders = new HttpHeaders();
                try {
                    httpHeaders.add("Access-Token", getAccessToken());
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                httpHeaders.add("Authorization",
                    "Bearer " + JwtUtil.createJWT(claimsMap));

                sisScheduleList
                    .stream()
                    .filter(s -> s.getScId().equals(sjc.getScId()))
                    .forEach(s -> {
                        try {
                            String jsonObject1 =
                                restTemplate.postForObject(String.format(
                                    "https://api.xsix103.cn/sign_in_system/v3" +
                                        "/schedules/%s/signIns/doSignIn",
                                    s.getSsId()),
                                    new HttpEntity<String>(httpHeaders),
                                    String.class);
                            log.info(jsonObject1);
                        } catch (HttpClientErrorException e) {
                            log.error(new String(e.getResponseBodyAsByteArray()));
                        }
                    });
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
