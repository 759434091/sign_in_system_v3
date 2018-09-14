package service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.service.LoginService;
import team.a9043.sign_in_system_version_2.util.UserPasswordEncrypt;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
public class LoginServiceTest {
    @Autowired
    private LoginService loginService;

    @Test
    @Transactional
    public void changePassword() {
        User user = new User("2016220401001", null, null, null, null);
        loginService.changePassword(user, UserPasswordEncrypt.encrypt("123456"));
    }
}
