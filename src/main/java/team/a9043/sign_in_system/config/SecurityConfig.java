package team.a9043.sign_in_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.a9043.sign_in_system.security.*;
import team.a9043.sign_in_system.service.SecurityUserDetailService;

import javax.annotation.Resource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private SecurityUserDetailService securityUserDetailService;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private DaoAuthenticationProvider getDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(securityUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(getDaoAuthenticationProvider())
                .eraseCredentials(false);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("suId")
                .passwordParameter("suPassword")
                .loginProcessingUrl("/tokens")
                .successHandler(new SisAuthenticationSuccessHandler())
                .failureHandler(new SisAuthenticationFailureHandler())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new SisAuthenticationEntryPoint())
                .accessDeniedHandler(new SisAccessDeniedHandler())
                .and()
                .addFilterBefore(new SisAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
    }
}
