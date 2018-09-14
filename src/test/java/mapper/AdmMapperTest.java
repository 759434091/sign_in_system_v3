package mapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.mapper.AdmMapper;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.pojo.extend.SuvRecWithSuv;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class AdmMapperTest {
    @Value("#{${cozMap}}")
    private Map<String, String> string;
    @Resource
    private AdmMapper admMapper;

    @Test
    public void getSupervisor() {

        List<User> userList = admMapper.getSupervisor();
        JSONArray jsonArray = new JSONArray(userList);
        System.out.println(jsonArray.toString());
    }

    @Test
    public void getCourseCount() {
        String cozName = "计网";
        String[] cozChar = cozName.split("");
        StringBuilder cozSearch = new StringBuilder();
        for (int i = 0; ; i++) {
            if (i == cozChar.length) {
                cozSearch.append("%");
                break;
            }
            cozSearch.append("%").append(cozChar[i]);
        }
        int count = admMapper.getCourseCount(cozSearch.toString(), null, null);
        System.out.println((count));
    }

    @Test
    public void getCourse() {
        String cozName = "计网";
        String[] cozChar = cozName.split("");
        StringBuilder cozSearch = new StringBuilder();
        for (int i = 0; ; i++) {
            if (i == cozChar.length) {
                cozSearch.append("%");
                break;
            }
            cozSearch.append("%").append(cozChar[i]);
        }
        List<Course> courseList = admMapper.getCourse(cozSearch.toString(), 0, 8, "coz_name", true, null, null);
        JSONObject.valueToString(courseList);
        System.out.println(JSONObject.valueToString(courseList));
    }

    @Test
    public void getCozStudent() {
        List<User> userList = admMapper.getCozStudent("E0911835.02");
        System.out.println(JSONObject.valueToString(userList));
    }

    @Test
    public void getCozSuvRec() {
        List<SuvRecWithSuv> suvRecWithSuvList = admMapper.getCozSuvRec(2017, true, "E0911835.02");
        System.out.println(JSONObject.valueToString(suvRecWithSuvList));
    }

    @Test
    public void getStaAttRate() {
        Map map = admMapper.getStaAttRate();
        System.out.println(JSONObject.valueToString(map));
    }


}
