package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.exception.InvalidPermissionException;
import team.a9043.sign_in_system.pojo.SisMonitorTrans;
import team.a9043.sign_in_system.pojo.SisSchedule;
import team.a9043.sign_in_system.pojo.SisSupervision;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.util.judgetime.InvalidTimeParameterException;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;
import team.a9043.sign_in_system.util.judgetime.ScheduleParserException;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ExecutionException;

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
    public void getCourses() throws ExecutionException, InterruptedException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        JSONObject jsonObject = monitorService.getCourses(sisUser);
        log.info(jsonObject.toString(2));
    }

    @Test
    @Transactional
    public void insertSupervision() throws IncorrectParameterException,
        ScheduleParserException, InvalidPermissionException,
        InvalidTimeParameterException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");

        Integer ssId = 2;

        SisSchedule sisSchedule = new SisSchedule();
        sisSchedule.setSsId(ssId);
        sisSchedule.setSsDayOfWeek(DayOfWeek.TUESDAY.getValue());
        sisSchedule.setSsStartTime(1);

        SisSupervision sisSupervision = new SisSupervision();
        sisSupervision.setSsId(ssId);
        sisSupervision.setSsvWeek(1);
        sisSupervision.setSsvActualNum(1);
        sisSupervision.setSsvMobileNum(1);
        sisSupervision.setSsvSleepNum(1);
        sisSupervision.setSsvRecInfo(":");

        LocalDate localDate = JudgeTimeUtil.getScheduleDate(sisSchedule, 1);
        LocalTime localTime =
            JudgeTimeUtil.getClassTime(sisSchedule.getSsStartTime());

        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        JSONObject jsonObject = monitorService.insertSupervision(sisUser, ssId,
            sisSupervision,
            localDateTime);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getSupervisions() {
    }

    @Test
    public void modifyMonitor() {
    }

    @Test
    @Transactional
    public void applyForTransfer() throws IncorrectParameterException,
        InvalidPermissionException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");

        SisUser tSisUser = new SisUser();
        tSisUser.setSuId("Z");

        SisMonitorTrans sisMonitorTrans = new SisMonitorTrans();
        sisMonitorTrans.setSuId("2016220401001");
        sisMonitorTrans.setSsId(2);
        sisMonitorTrans.setSmtWeek(2);

        monitorService.applyForTransfer(sisUser, 2, sisMonitorTrans);
    }
}