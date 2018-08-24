package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.entity.SisSchedule;
import team.a9043.sign_in_system.entity.SisSignIn;
import team.a9043.sign_in_system.entity.SisSignInDetail;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.repository.SisSignInDetailRepository;
import team.a9043.sign_in_system.repository.SisSignInRepository;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class SignInServiceTest {
    @Resource
    private SisSignInRepository sisSignInRepository;
    @Resource
    private SisSignInDetailRepository sisSignInDetailRepository;
    @Resource
    private SignInService signInService;

    @Test
    public void test() {
        SisSchedule sisSchedule = new SisSchedule();
        sisSchedule.setSsId(2);
        SisSignIn sisSignIn = new SisSignIn();
        sisSignIn.setSisSchedule(sisSchedule);
        sisSignIn.setSsiWeek(1);

        ArrayList<SisSignInDetail> sisSignInDetails = new ArrayList<>();
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        SisSignInDetail sisSignInDetail = new SisSignInDetail();
        sisSignInDetail.setSisUser(sisUser);
        sisSignInDetail.setSisSignIn(sisSignIn);
        sisSignInDetail.setSsidStatus(true);
        sisSignInDetails.add(sisSignInDetail);

        sisSignIn.setSisSignInDetails(sisSignInDetails);

        sisSignInRepository.save(sisSignIn);
        sisSignInDetailRepository.saveAll(sisSignInDetails);
    }

    @Test
    public void getSignIn() {
        JSONObject jsonObject = signInService.getSignIn(2, 1);
        log.info(jsonObject.toString(2));
    }

    @Test
    public void getSignIns() throws IncorrectParameterException {
        JSONObject jsonObject = signInService.getSignIns("A");
        log.info(jsonObject.toString(2));
    }
}