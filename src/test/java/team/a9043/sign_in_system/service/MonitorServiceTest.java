package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.entity.SisUser;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class MonitorServiceTest {
    @Resource
    private MonitorService monitorService;

    @Test
    public void getCourses() {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        JSONObject jsonObject = monitorService.getCourses(sisUser);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void insertSupervision() {
    }

    @Test
    public void getSupervisions() {
    }
}