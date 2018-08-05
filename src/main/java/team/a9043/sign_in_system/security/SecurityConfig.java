package team.a9043.sign_in_system.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.a9043.sign_in_system.security.handler.SisAuthenticationEntryPoint;
import team.a9043.sign_in_system.security.handler.SisAuthenticationFailureHandler;
import team.a9043.sign_in_system.security.handler.SisAuthenticationSuccessHandler;
import team.a9043.sign_in_system.security.service.SecurityUserDetailService;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private SecurityUserDetailService securityUserDetailService;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private DaoAuthenticationProvider getDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider =
            new DaoAuthenticationProvider();
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
            .antMatchers("/tokens/{code}").permitAll()
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
            .and()
            .addFilterBefore(new SisAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }
}
