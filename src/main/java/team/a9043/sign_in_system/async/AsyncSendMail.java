package team.a9043.sign_in_system.async;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.pojo.SisContact;

import javax.annotation.Resource;

/**
 * @author a9043
 */
@Component
@Slf4j
public class AsyncSendMail {
    @Resource
    private MailSender mailSender;
    @Async
    public void sendMail(SisContact sisContact) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("2541175183@qq.com");
        mailMessage.setSubject(sisContact.getSctName());
        mailMessage.setText(new JSONObject(sisContact).toString(2));
        mailMessage.setTo("a90434957@live.cn");
        mailSender.send(mailMessage);
        log.info("send email :" + sisContact.getSctContact());
    }
}
