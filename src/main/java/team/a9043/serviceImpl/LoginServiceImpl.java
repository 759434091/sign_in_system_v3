package team.a9043.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.a9043.mapper.UserMapper;
import team.a9043.pojo.User;
import team.a9043.service.LoginService;
import team.a9043.JavaUtil.UserPasswordEncrypt;

@Service
public class LoginServiceImpl implements LoginService {

    private UserMapper userMapper;

    @Autowired
    public LoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User userLogin(String userId, String userPwd) {
        String encryptUserPwd = UserPasswordEncrypt.encrypt(userPwd);
        User user;
        if ((user = userMapper.fByStuNameAndPwd(userId, encryptUserPwd)) != null) {
            return user;
        } else {
            return userMapper.fByTchNameAndPwd(userId, encryptUserPwd);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User wxLogin(String openId) {
        User user = userMapper.fByStuOpenId_stu(openId);
        if (user == null) {
            return userMapper.fByStuOpenId_tch(openId);
        } else {
            return user;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean bindOpenId(String openId, String userId) {
        return userMapper.bindOpenId_stu(openId, userId) || userMapper.bindOpenId_tch(openId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean changePassword(String userId, String userPwd) {
        return userMapper.changeStuPassword(userId, userPwd) || userMapper.changeTchPassword(userId, userPwd);
    }

}
