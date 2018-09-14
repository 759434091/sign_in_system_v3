package team.a9043.sign_in_system.config;

import org.apache.catalina.core.AprLifecycleListener;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author a9043
 */
@Configuration
public class AprConfig {
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat =
            new TomcatServletWebServerFactory();
        tomcat.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
        tomcat.addContextLifecycleListeners(new AprLifecycleListener());
        return tomcat;
    }
}
