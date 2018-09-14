package team.a9043.sign_in_system.service;

import org.json.JSONObject;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.mapper.SisContactMapper;
import team.a9043.sign_in_system.pojo.SisContact;

import javax.annotation.Resource;

@Service
public class ContactService {
    @Resource
    private SisContactMapper sisContactMapper;
    @Resource
    private MailSender mailSender;

    public boolean receiveKssContact(SisContact sisContact) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("2541175183@qq.com");
        mailMessage.setSubject(sisContact.getSctName());
        mailMessage.setText(new JSONObject(sisContact).toString(2));
        mailMessage.setTo("a90434957@live.cn");
        mailSender.send(mailMessage);
        return sisContactMapper.insert(sisContact) > 0;
    }
}
