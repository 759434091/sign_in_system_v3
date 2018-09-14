package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.pojo.SisUser;

import javax.annotation.Resource;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SignInServiceTest {
    @Resource
    private SignInService signInService;

    @Test
    public void getSignInsUser() throws IncorrectParameterException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        JSONObject jsonObject = signInService.getSignIns(sisUser, "A");
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getSignIn() {
        JSONObject jsonObject = signInService.getSignIn(2, 1);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getSignIns() throws IncorrectParameterException {
        JSONObject jsonObject = signInService.getSignIns("A");
        log.info(jsonObject.toString(2));
    }
}