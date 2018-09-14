package service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.Course;
import team.a9043.sign_in_system_version_2.pojo.Supervision;
import team.a9043.sign_in_system_version_2.pojo.extend.ScheduleWithCozDtl;
import team.a9043.sign_in_system_version_2.service.AdmService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class AdmServiceTest {
    @Autowired
    AdmService admService;

    @Test
    public void getSupervision() {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        List<Supervision> supervisionList = admService.getSupervision(localDateTime, 8);
        System.out.println(JSONObject.valueToString(supervisionList));
    }

    @Test
    public void getCourse() {
        List<Course> courseList = null;
        courseList = admService.getCourse("计网", 1, 8, "cozName", true,null,null);
        System.out.println(JSONObject.valueToString(courseList));
    }

    @Test
    public void getHistorySignIn() throws ParameterNotFoundException {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        JSONArray scheduleWithHistorySignInList = admService.getCozHistorySignIn("E0911835.02", localDateTime);
        System.out.println(JSONObject.valueToString(scheduleWithHistorySignInList));
    }

    @Test
    public void insertSupervision() throws ParameterNotFoundException {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        Supervision supervision = new Supervision();
        ScheduleWithCozDtl scheduleWithCozDtl = new ScheduleWithCozDtl();
        scheduleWithCozDtl.setSchId(1);
        supervision.setScheduleWithCozDtl(scheduleWithCozDtl);
        supervision.setSuvWeek(9);
        //System.out.println(admService.insertSupervision(supervision, localDateTime));
    }

    @Test
    @Transactional
    public void readCozExcel() {
        File file = new File("H:\\上线测试doc\\教学任务列表.xls");
        admService.readCozExcel(file, "");
    }

    @Test
    @Transactional
    public void readStuExcel() {
        File file = new File("H:\\上线测试doc\\上课名单导出-2.xls");
        admService.readStuExcel(file, "");
    }
}
