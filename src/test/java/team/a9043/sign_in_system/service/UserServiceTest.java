package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    @Transactional
    public void createUser() {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        sisUser.setSuPassword("123456");
        sisUser.setSuAuthoritiesStr("STUDENT");
        sisUser.setSuName("卢学能");
        boolean res = userService.createUser(sisUser);
        log.info("res " + res);
    }

    @Test
    public void getTokensByCode() throws WxServerException {
        JSONObject jsonObject = userService.getTokensByCode("123456");
        log.info(jsonObject.toString(2));
    }

    @Test
    public void testToken() {
        String aToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
            ".eyJpc3MiOiJhOTA0MyIsInN1SWQiOiIyMDE2MjIwNDAxMDAxIiwic3VBdXRob3JpdGllc1N0ciI6IlNUVURFTlQiLCJleHAiOjE1MzUyMDc2MjIsInN1TmFtZSI6IuWNouWtpuiDvSJ9.X96XQ-BrYk4zWc1HNK7TD9vJOf9fcw73suRaKzug_UY3xAGR2w2-oM_TJwEbFTtZVoqEVDZXKTbbrmtr74kykg";
        String bToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
            ".eyJpc3MiOiJhOTA0MyIsInN1SWQiOiIyMDE2MjIwNDAxMDAxIiwic3VBdXRob3JpdGllc1N0ciI6IlNUVURFTlQiLCJleHAiOjE1MzUyMDc3NjcsInN1TmFtZSI6IjIwMTYyMjA0MDEwMDEifQ.BbaUts5SgMi7HXXYH-RZ0oFuEMSHDpXgsKF7xw8w7Cl_byxzu8_-fQl2lYu0CN8GeMwh2bdbOmuqDTgk_-U03Q";

        JSONObject a = new JSONObject(JwtUtil.parseJwt(aToken));
        JSONObject b = new JSONObject(JwtUtil.parseJwt(bToken));
        log.info(a.toString(2));
        log.info(b.toString(2));
    }
}