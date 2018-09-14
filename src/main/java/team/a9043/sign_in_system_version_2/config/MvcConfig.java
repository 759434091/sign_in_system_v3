package team.a9043.sign_in_system_version_2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import team.a9043.sign_in_system_version_2.interceptor.AdmInterceptor;
import team.a9043.sign_in_system_version_2.interceptor.NoCacheInterceptor;
import team.a9043.sign_in_system_version_2.interceptor.StuInterceptor;
import team.a9043.sign_in_system_version_2.interceptor.SuvInterceptor;

import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private AdmInterceptor admInterceptor;
    private SuvInterceptor suvInterceptor;
    private StuInterceptor stuInterceptor;
    private NoCacheInterceptor noCacheInterceptor;

    @Autowired
    public MvcConfig(AdmInterceptor admInterceptor, SuvInterceptor suvInterceptor, StuInterceptor stuInterceptor, NoCacheInterceptor noCacheInterceptor) {
        this.admInterceptor = admInterceptor;
        this.suvInterceptor = suvInterceptor;
        this.stuInterceptor = stuInterceptor;
        this.noCacheInterceptor = noCacheInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(noCacheInterceptor).addPathPatterns("/**");
        registry.addInterceptor(admInterceptor).addPathPatterns("/administrator/**");
        registry.addInterceptor(suvInterceptor).addPathPatterns("/supervisor/**");
        registry.addInterceptor(stuInterceptor).addPathPatterns("/student/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "PATCH", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(false).maxAge(3600);
    }

}
