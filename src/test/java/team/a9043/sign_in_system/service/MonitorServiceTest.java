package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisSupervision;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.util.judgetime.ScheduleParerException;

import javax.annotation.Resource;

import java.time.LocalDateTime;

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
    public void insertSupervision() throws IncorrectParameterException,
        ScheduleParerException, InvalidPermissionException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        Integer ssId = 2;
        SisSchedule sisSchedule = new SisSchedule();
        sisSchedule.setSsId(2);
        SisSupervision sisSupervision = new SisSupervision();
        sisSupervision.setSisSchedule(sisSchedule);
        sisSupervision.setSsvWeek(1);
        sisSupervision.setSsvActualNum(1);
        sisSupervision.setSsvMobileNum(1);
        sisSupervision.setSsvRecInfo(":");
        sisSupervision.setSsvSleepNum(1);
        JSONObject jsonObject = monitorService.insertSupervision(sisUser, ssId,
            sisSupervision,
            LocalDateTime.now());
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getSupervisions() {
    }
}