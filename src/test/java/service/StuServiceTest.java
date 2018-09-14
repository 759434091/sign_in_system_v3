package service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.Location;
import team.a9043.sign_in_system_version_2.pojo.Schedule;
import team.a9043.sign_in_system_version_2.pojo.User;
import team.a9043.sign_in_system_version_2.pojo.extend.StuTimetable;
import team.a9043.sign_in_system_version_2.service.StuService;

import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class StuServiceTest {
    @Autowired
    StuService stuService;
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void getStuTimetable() {
        User user = new User("2016220401001", null, null, null, null);
        StuTimetable stuTimetable = stuService.getStuTimetable(user);
        System.out.println(JSONObject.valueToString(stuTimetable));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void SignIn() {
        User user = new User("2016220401001", null, null, null, null);
        Location location = new Location();
        location.setLocLat(30.6746773009);
        location.setLocLong(104.1028136015);
        Schedule schedule = new Schedule();
        schedule.setSchId(1);
        schedule.setSchSignInWeek(8);
        schedule.setLocation(location);
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        JSONObject jsonObject = null;
        try {
            jsonObject = stuService.signIn(user, schedule, localDateTime);
        } catch (ParameterNotFoundException e) {
            e.printStackTrace();
        }
        String key = "signIn_" + user.getUsrId() + "_" + schedule.getSchId() + "_" + schedule.getSchSignInWeek();
        System.out.println(jsonObject.toString() + "       " + redisTemplate.delete(key));
    }

    @Test
    @Transactional
    public void leave() {
        User user = new User("2016220401001", null, null, null, null);
        Schedule schedule = new Schedule();
        schedule.setSchId(1);
        schedule.setSchSignInWeek(8);
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        MultipartFile voucher = new MockMultipartFile("test", "test".getBytes());
        JSONObject jsonObject = null;
        try {
            jsonObject = stuService.leave(user, voucher, schedule, localDateTime);
        } catch (ParameterNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString());
    }

    @Test
    public void getHistorySignIn() throws ParameterNotFoundException {
        User user = new User("2016220401001", null, null, null, null);
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        Course course = new Course();
        course.setCozId("E0911835.02");
        JSONArray jsonArray = stuService.getHistorySignIn(user, course, localDateTime);
        System.out.println(jsonArray.toString());
    }
}
