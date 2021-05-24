package com.emoney.pointweb.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    private int corePoolSize = 10;//核心线程数

    private int maxPoolSize = 50;//最大线程数

    private int queueCapacity = 1000;//队列最大长度

    private int keepAliveSeconds = 300;//线程池维护线程所允许的空闲时间

    private ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();//线程池对拒绝任务(无线程可用)的处理策略

    private String threadNamePrefix = "AsyncExecutorThread-";

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(callerRunsPolicy);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(callerRunsPolicy);
        executor.initialize();
        return executor;
    }
}
