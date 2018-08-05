package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.WxServerException;

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
}