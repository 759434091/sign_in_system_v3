package mapper;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.mapper.SupervisionMapper;
import team.a9043.sign_in_system_version_2.pojo.SignIn;
import team.a9043.sign_in_system_version_2.pojo.Supervision;
import team.a9043.sign_in_system_version_2.pojo.SuvTrans;
import team.a9043.sign_in_system_version_2.pojo.extend.SignInRecOnLeaveWithCozDtl;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class SupervisionMapperTest {
    @Resource
    private SupervisionMapper supervisionMapper;

    @Test
    public void getSuvCourseList() {
        List<Supervision> supervisionListWithStu = supervisionMapper.getSuvCourseList(2017, true, 8,false, "2016220401001");
        //List<Supervision> supervisionListWithNull = supervisionMapper.getSuvCourseList(2017, true, 9, false,null);
        System.out.println(JSONObject.valueToString(supervisionListWithStu));
    }

    @Test
    public void getStuLeaveRec() {
        List<SignInRecOnLeaveWithCozDtl> signInRecOnLeaveWithCozDtlList = supervisionMapper.getStuLeaveRec(2017, true, "2016220401001");
        System.out.println(JSONObject.valueToString(signInRecOnLeaveWithCozDtlList));
    }

    @Test
    public void getSignInBySchAndWeek() {
        SignIn signIn = supervisionMapper.getSignInBySchAndWeek(2017, true, 8, 1);
        System.out.println(JSONObject.valueToString(signIn));
    }

    @Test
    public void getSuvTrans() {
        List<SuvTrans> suvTransList = supervisionMapper.getSuvTransByUser(2017, true, "2016220401002");
        System.out.println(JSONObject.valueToString(suvTransList));
    }

    @Test
    public void getSuvRec() {
        SuvRecWithSuv suvRecWithSuvList = supervisionMapper.getSuvRec(2017, true, 1);
        System.out.println(JSONObject.valueToString(suvRecWithSuvList));
    }
}
