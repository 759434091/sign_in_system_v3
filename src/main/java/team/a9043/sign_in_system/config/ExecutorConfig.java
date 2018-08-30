package team.a9043.sign_in_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.TaskUtils;

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
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(60);
        taskExecutor.setRejectedExecutionHandler(callerRunsPolicy);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler =
            new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.setRejectedExecutionHandler(callerRunsPolicy);
        threadPoolTaskScheduler.setErrorHandler(TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
