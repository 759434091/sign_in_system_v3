package service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.sign_in_system_version_2.SignInSystemVersion2Application;
import team.a9043.sign_in_system_version_2.exception.InsufficientPermissionsException;
import team.a9043.sign_in_system_version_2.exception.InvalidParameterException;
import team.a9043.sign_in_system_version_2.exception.ParameterNotFoundException;
import team.a9043.sign_in_system_version_2.pojo.*;
import team.a9043.sign_in_system_version_2.service.SuvService;
import team.a9043.sign_in_system_version_2.util.PowerOperation;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SignInSystemVersion2Application.class)
@ActiveProfiles("test")
public class SuvServiceTest {
    private Logger logger = LoggerFactory.getLogger(SuvServiceTest.class);
    @Autowired
    SuvService suvService;

    @Test
    @Transactional
    public void insertSuvRec() {
        User user = new User();
        user.setUsrId("2016220401001");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        SuvRec suvRec = new SuvRec();
        suvRec.setSupervisionSuvId(1);
        suvRec.setSuvRecNum(1);
        suvRec.setSuvRecBadNum(1);
        suvRec.setSuvRecInfo("test");
        suvRec.setSuvRecName("我");
        boolean res = false;
        try {
            res = suvService.insertSuvRec(user, suvRec, localDateTime);
        } catch (ParameterNotFoundException | InsufficientPermissionsException e) {
            e.printStackTrace();
        }
        logger.info("" + res);
    }

    @Test
    @Transactional
    public void approveOrRejectLeave() {
        User user = new User();
        user.setUsrId("2016220401001");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        SignInRec signInRec = new SignInRec();
        signInRec.setSirId(3);
        signInRec.setSignInSiId(1);
        signInRec.setSirApprove(1);
        boolean res = false;
        try {
            res = suvService.approveOrRejectLeave(user, signInRec, localDateTime);
        } catch (InsufficientPermissionsException | ParameterNotFoundException | InvalidParameterException e) {
            e.printStackTrace();
        }
        logger.info("" + res);
    }

    @Test
    @Transactional
    public void changeSignIn() {
        User user = new User();
        user.setUsrId("2016220401001");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        SignIn signIn = new SignIn();
        signIn.setSiWeek(8);
        signIn.setScheduleSchId(1);
        signIn.setSiTime(LocalDateTime.of(1970, 1, 1, 0, 0, 1));
        signIn.setSiAuto(true);
        boolean res = false;
        try {
            res = suvService.changeSignIn(user, signIn, localDateTime);
        } catch (InsufficientPermissionsException | ParameterNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("" + res);
    }

    @Test
    @Transactional
    public void changePower() {
        User user = new User();
        user.setUsrId("2016220401001");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        Supervision supervision = new Supervision();
        supervision.setSuvId(1);
        supervision.setStudent(user);
        boolean res = false;
        try {
            res = suvService.changePower(user, PowerOperation.GIVE_UP, supervision, localDateTime);
        } catch (ParameterNotFoundException | InsufficientPermissionsException e) {
            e.printStackTrace();
        }
        logger.info("" + res);
    }

    @Test
    @Transactional
    public void insertSuvTrans() {
        User user = new User();
        user.setUsrId("2016220401001");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        Supervision supervision = new Supervision();
        supervision.setSuvId(1);
        supervision.setStudent(user);
        SuvTrans suvTrans = new SuvTrans();
        suvTrans.setSupervision(supervision);
        suvTrans.setUserUsrId("2016220401001");
        boolean res = false;
        try {
            res = suvService.insertSuvTrans(user, suvTrans, localDateTime);
        } catch (ParameterNotFoundException | InsufficientPermissionsException e) {
            e.printStackTrace();
        }
        logger.info("" + res);
    }

    @Test
    public void getSuvTrans() {
        User user = new User();
        user.setUsrId("2016220401002");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        List<SuvTrans> res;
        res = suvService.getSuvTrans(user, localDateTime);
        logger.info(JSONObject.valueToString(res));
    }

    @Test
    public void getSuvPool() {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 23, 14, 35, 0);
        JSONArray jsonArray = suvService.getSuvPool(localDateTime);
        logger.info(JSONObject.valueToString(jsonArray));
    }
}
