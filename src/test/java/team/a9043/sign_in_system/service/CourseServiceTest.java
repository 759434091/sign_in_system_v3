package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.pojo.SisCourse;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisUser;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CourseServiceTest {
    @Resource
    private CourseService courseService;

    @Test
    public void getCourses() throws
        ExecutionException, InterruptedException, IncorrectParameterException {
        LocalDateTime localDateTime = LocalDateTime.now();
        JSONObject jsonObject = courseService.getCourses(1, 10, true, null, null
            , null, null, null);
        LocalDateTime localDateTime2 = LocalDateTime.now();
        log.info(jsonObject.toString(2));
        log.info("until: " + localDateTime.until(localDateTime2,
            ChronoUnit.MILLIS));
    }

    @Test
    public void getCourses1() throws ExecutionException, InterruptedException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("3203604");
        JSONObject jsonObject = courseService.getCourses(sisUser, SisJoinCourse.JoinCourseType.TEACHING);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void modifySsNeedMonitor() throws IncorrectParameterException {
        SisCourse sisCourse = new SisCourse();
        sisCourse.setScId("A");
        sisCourse.setScNeedMonitor(true);
        JSONObject jsonObject = courseService.modifyScNeedMonitor(sisCourse);
        log.info(jsonObject.toString(2));
    }
}