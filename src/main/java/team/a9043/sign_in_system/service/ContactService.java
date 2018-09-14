package team.a9043.sign_in_system.service;

import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.mapper.SisContactMapper;
import team.a9043.sign_in_system.pojo.SisContact;

import javax.annotation.Resource;

@Service
public class ContactService {
    @Resource
    private SisContactMapper sisContactMapper;

    public boolean receiveKssContact(SisContact sisContact) {
        return sisContactMapper.insert(sisContact) > 0;
    }
}
