package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.entity.SisCourse;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class CourseServiceTest {
    @Resource
    private CourseService courseService;

    @Test
    public void getCourses() throws IncorrectParameterException {
        JSONObject jsonObject = courseService.getCourses(true, null, 2);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getCourses1() {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        JSONObject jsonObject = courseService.getCourses(sisUser);
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