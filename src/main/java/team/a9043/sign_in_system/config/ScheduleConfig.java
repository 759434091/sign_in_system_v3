package team.a9043.sign_in_system.config;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author a9043
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }
}
