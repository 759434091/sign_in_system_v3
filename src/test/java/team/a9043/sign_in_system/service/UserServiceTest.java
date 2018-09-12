package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.WxServerException;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.util.JwtUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
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
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("suId", sisUser.getSuId());
        claimsMap.put("suName", sisUser.getSuName());
        claimsMap.put("suAuthoritiesStr", sisUser.getSuAuthoritiesStr());
        claimsMap.put("type", "code");
        log.info("res " + JwtUtil.createJWT(claimsMap));
    }

    @Test
    public void getTokensByCode() throws WxServerException {
        JSONObject jsonObject = userService.getTokensByCode("123456");
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getStudents() throws IncorrectParameterException {
        JSONObject jsonObject = userService.getStudents(1, 10, null, null,
            true);
        log.info(jsonObject.toString(2));
    }
}