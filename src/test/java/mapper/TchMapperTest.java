package mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.mapper.TchMapper;
import team.a9043.sign_in_system_version_2.pojo.Course;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class TchMapperTest {
    @Resource
    private TchMapper tchMapper;

    @Test
    public void tchCoursesTest() {
        List<Course> courseList = tchMapper.selectCourseByTch("3203604");
        System.out.println(courseList);
    }
}
