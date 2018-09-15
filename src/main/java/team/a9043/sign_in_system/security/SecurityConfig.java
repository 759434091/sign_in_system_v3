package team.a9043.sign_in_system.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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
    private SisAuthenticationSuccessHandler sisAuthenticationSuccessHandler;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private DaoAuthenticationProvider getDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider =
            new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(securityUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return daoAuthenticationProvider;
    }

    private UrlBasedCorsConfigurationSource getUrlBasedCorsConfigurationSource() {
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
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
            .configurationSource(getUrlBasedCorsConfigurationSource())
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/v2/**").permitAll()
            .antMatchers("/swagger-ui.html/**").permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/tokens/{code}").permitAll()
            .antMatchers("/tokens").permitAll()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .usernameParameter("suId")
            .passwordParameter("suPassword")
            .loginProcessingUrl("/tokens")
            .successHandler(sisAuthenticationSuccessHandler)
            .failureHandler(new SisAuthenticationFailureHandler())
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(new SisAuthenticationEntryPoint())
            .and()
            .addFilterBefore(new SisAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }
}
