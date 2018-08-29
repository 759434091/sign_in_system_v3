package team.a9043.sign_in_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author a9043
 */
@Configuration
public class ExecutorConfig {
    @Bean
    public ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy() {
        return new ThreadPoolExecutor.CallerRunsPolicy();
    }

    @Bean
    public TaskExecutor taskExecutor(ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setRejectedExecutionHandler(callerRunsPolicy);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler =
            new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(50);
        threadPoolTaskScheduler.setErrorHandler(TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
