package team.a9043.sign_in_system.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.a9043.sign_in_system.entity.SisUser;
import team.a9043.sign_in_system.repository.SisUserRepository;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @author a9043
 */
@Service
public class UserService {
    @Resource
    private SisUserRepository sisUserRepository;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 新增用户
     *
     * @param sisUser 表单用户
     * @return 操作结果
     */
    @Transactional
    public boolean createUser(SisUser sisUser) {
        sisUser.setSuPassword(bCryptPasswordEncoder.encode(sisUser.getSuPassword()));
        return null != sisUserRepository.save(sisUser);
    }
}
