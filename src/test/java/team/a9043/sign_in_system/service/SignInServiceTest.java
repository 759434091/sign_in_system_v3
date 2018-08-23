package team.a9043.sign_in_system.service;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class SignInServiceTest {
    @Test
    public void test() {
        log.info("start!");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("task start!");
            }
        }, 2 * 1000,1000);
    }
}