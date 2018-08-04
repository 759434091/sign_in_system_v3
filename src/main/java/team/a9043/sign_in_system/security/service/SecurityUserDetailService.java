package team.a9043.sign_in_system.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import team.a9043.sign_in_system.repository.SisUserRepository;
import team.a9043.sign_in_system.security.entity.SecurityUserDetails;

import javax.annotation.Resource;

@Component
public class SecurityUserDetailService implements UserDetailsService {
    @Resource
    private SisUserRepository sisUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return sisUserRepository
            .findById(username)
            .map(SecurityUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("no user " + username));
    }
}
