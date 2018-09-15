package team.a9043.sign_in_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
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
    private ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private CourseService courseService;

    @Test
    public void getCourses() throws
        ExecutionException, InterruptedException, IncorrectParameterException
        , JsonProcessingException {
        LocalDateTime localDateTime = LocalDateTime.now();
        PageInfo<SisCourse> pageInfo = courseService.getCourses(1,
            10,
            null,
            null,
            null,
            null,
            null,
            null);
        LocalDateTime localDateTime2 = LocalDateTime.now();
        log.info(objectMapper.writeValueAsString(pageInfo));
        log.info("until: " + localDateTime.until(localDateTime2,
            ChronoUnit.MILLIS));
    }

    @Test
    public void getCourses1() throws ExecutionException, InterruptedException, JsonProcessingException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("3203604");
        PageInfo<SisJoinCourse> pageInfo = courseService.getCourses(sisUser,
            SisJoinCourse.JoinCourseType.TEACHING);
        log.info(objectMapper.writeValueAsString(pageInfo));
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