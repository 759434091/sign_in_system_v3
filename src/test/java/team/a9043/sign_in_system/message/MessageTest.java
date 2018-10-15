package team.a9043.sign_in_system.message;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.service.MessageService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageTest {
    @Resource
    private MessageService messageService;

    @Test
    public void sendTest2() throws InterruptedException {
        LocalDateTime localDateTime = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);
        messageService.sendSignInMessage(511, localDateTime);
        Thread.sleep(10000);
    }
}
