package team.a9043.sign_in_system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.async.AsyncSendMail;
import team.a9043.sign_in_system.mapper.SisContactMapper;
import team.a9043.sign_in_system.pojo.SisContact;
import team.a9043.sign_in_system.service_pojo.OperationResponse;

import javax.annotation.Resource;

@Service
@Slf4j
public class ContactService {
    @Resource
    private SisContactMapper sisContactMapper;
    @Resource
    private AsyncSendMail asyncSendMail;

    public OperationResponse receiveKssContact(SisContact sisContact) {
        asyncSendMail.sendMail(sisContact);
        sisContactMapper.insert(sisContact);
        return OperationResponse.SUCCESS;
    }
}
