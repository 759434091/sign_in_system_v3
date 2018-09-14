package mapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.mapper.CourseMapper;
import team.a9043.sign_in_system_version_2.pojo.SignIn;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithHistorySignIn;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInWithCozDtl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class StuMapperTest {
    @Resource
    private CourseMapper courseMapper;

    @Test
    public void getSignInTest() {
        SignIn signIn1 = courseMapper.getSignInByWeekAndSch(2017, true, 1, 8);
        System.out.println(JSONObject.valueToString(signIn1));
    }

    @Test
    public void getHistorySignIn() {
        List<ScheduleWithHistorySignIn> scheduleWithHistorySignInList = courseMapper.getHistorySignIn(2017,
                true,
                "E0911835.02",
                "2016220401001");
        System.out.println(JSONObject.valueToString(scheduleWithHistorySignInList));
    }

    @Test
    public void getNeedSignInByUser() {
        List<SignInWithCozDtl> scheduleWithNeedSignInList = courseMapper.getNeedSignInByUser(2017, true, "2016220401001");
        System.out.println(JSONObject.valueToString(scheduleWithNeedSignInList));
    }

    @Test
    public void test() throws JSONException {
    }
}
