package com.pioneer.framework.manager;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 *
 * @author hlm
 * @date 2021-08-09 17:48:27
 */
@Slf4j
public class AsyncManager {

    /**
     * 异步操作任务调度线程池
     */
    private final ScheduledExecutorService executor = SpringUtil.getBean(ScheduledExecutorService.class);

    /**
     * 单例模式
     */
    private AsyncManager() {
    }

    private static final AsyncManager ME = new AsyncManager();

    public static AsyncManager me() {
        return ME;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task) {
        executor.schedule(task, 10, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
            try {
                long timeout = 120;
                if (!executor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                        log.info("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
