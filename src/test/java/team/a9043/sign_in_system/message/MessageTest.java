package team.a9043.sign_in_system.message;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import team.a9043.sign_in_system.convertor.JsonObjectHttpMessageConverter;
import team.a9043.sign_in_system.mapper.SisUserMapper;
import team.a9043.sign_in_system.pojo.SisUser;
import team.a9043.sign_in_system.service.MessageService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageTest {
    @Resource
    private MessageService messageService;
    @Resource
    private SisUserMapper sisUserMapper;

    @Test
    public void sendTest2() throws InterruptedException {
        LocalDateTime localDateTime = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);
        messageService.sendSignInMessage("R0900520.01", localDateTime);
        Thread.sleep(100000);
    }

    @Test
    public void sendTest() throws InterruptedException {
        SisUser sisUser = sisUserMapper.selectByPrimaryKey("2016220401001");
        messageService.sendSignInMessage(sisUser);
        Thread.sleep(10000);
    }
}
