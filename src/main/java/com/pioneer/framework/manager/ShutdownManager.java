package com.pioneer.framework.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 确保应用退出时能关闭后台线程
 *
 * @author hlm
 * @date 2021-08-09 17:48:32
 */
@Slf4j
@Component
public class ShutdownManager {

    /**
     * 停止异步执行任务
     */
    @PreDestroy
    public void destroy() {
        try {
            log.info("====关闭后台任务任务线程池====");
            AsyncManager.me().shutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
