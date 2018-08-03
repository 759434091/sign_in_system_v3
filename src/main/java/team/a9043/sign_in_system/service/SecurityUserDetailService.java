package team.a9043.sign_in_system.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.entity.SecurityUserDetails;
import team.a9043.sign_in_system.entity.SisUser;

import javax.annotation.Resource;

@Component
public class SecurityUserDetailService implements UserDetailsService {
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SisUser sisUser = new SisUser();
        sisUser.setSuId("2016220401001");
        sisUser.setSuEnPassword(bCryptPasswordEncoder.encode("123456"));
        sisUser.setSuName("卢学能");
        sisUser.setSuOpenid("openid");

        return sisUser.getSuId().equals(username) ? new SecurityUserDetails(sisUser) : null;
    }
}
