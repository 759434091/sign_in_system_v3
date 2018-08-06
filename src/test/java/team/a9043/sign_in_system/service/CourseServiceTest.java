package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.entity.SisUser;

import javax.annotation.Resource;
import java.time.LocalTime;

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
    public void getCourses() {
        JSONObject jsonObject = courseService.getCourses(0);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getCourses1() {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        LocalTime start = LocalTime.now();
        JSONObject jsonObject = courseService.getCourses(sisUser);
        LocalTime end1 = LocalTime.now();
        log.info(jsonObject.toString(2));
        LocalTime end2 = LocalTime.now();
        log.info("use " + (end1.toNanoOfDay() - start.toNanoOfDay()) / 1000000);
        log.info("use " + (end2.toNanoOfDay() - start.toNanoOfDay()) / 1000000);
    }
}