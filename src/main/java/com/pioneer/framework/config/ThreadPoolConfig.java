package com.pioneer.framework.config;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author hlm
 * @date 2021-08-09 17:47:55
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    /**
     * 核心线程池大小
     */
    private final int corePoolSize = 50;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        // 最大可创建的线程数
        executor.setMaxPoolSize(200);
        // 队列最大长度
        executor.setQueueCapacity(1000);
        // 线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(300);
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                ThreadUtil.createThreadFactoryBuilder().setNamePrefix("schedule-pool-%d").setDaemon(true).build()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t != null) {
                    log.error(t.getMessage(), t);
                }
            }
        };
    }
}
