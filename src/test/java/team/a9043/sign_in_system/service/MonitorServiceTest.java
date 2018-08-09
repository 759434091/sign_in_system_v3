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
import team.a9043.sign_in_system.repository.SisScheduleRepository;
import team.a9043.sign_in_system.util.judgetime.JudgeTimeUtil;
import team.a9043.sign_in_system.util.judgetime.ScheduleParserException;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class MonitorServiceTest {
    @Resource
    private MonitorService monitorService;
    @Resource
    private SisScheduleRepository sisScheduleRepository;

    @Test
    public void getCourses() {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        JSONObject jsonObject = monitorService.getCourses(sisUser);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void insertSupervision() throws IncorrectParameterException,
        ScheduleParserException, InvalidPermissionException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        Integer ssId = 2;
        SisSchedule sisSchedule =
            sisScheduleRepository.findById(2).orElseThrow(() -> new IncorrectParameterException(""));
        SisSupervision.IdClass idClass = new SisSupervision.IdClass();
        idClass.setSisSchedule(sisSchedule);
        idClass.setSsvWeek(1);
        SisSupervision sisSupervision = new SisSupervision();
        sisSupervision.setSsvId(idClass);
        sisSupervision.setSsvActualNum(1);
        sisSupervision.setSsvMobileNum(1);
        sisSupervision.setSsvSleepNum(1);
        sisSupervision.setSsvRecInfo(":");

        LocalDate localDate = JudgeTimeUtil.getScheduleDate(sisSchedule, 1);
        LocalTime localTime =
            JudgeTimeUtil.getClassTime(sisSchedule.getSsStartTime());
        assert localDate != null;
        assert localTime != null;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        JSONObject jsonObject = monitorService.insertSupervision(sisUser, ssId,
            sisSupervision,
            localDateTime);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getSupervisions() {
    }
}