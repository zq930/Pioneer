package com.pioneer.generator.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 *
 * @author hlm
 * @date 2021-08-12 12:54:04
 */
@Data
@Component
public class GenConfig {

    /**
     * 作者
     */
    public static String author = "author";

    /**
     * 生成包路径
     */
    public static String packageName = "com.pioneer.web.system";

    /**
     * 自动去除表前缀，默认是false
     */
    public static boolean autoRemovePre = false;

    /**
     * 表前缀(类名不会包含表前缀)
     */
    public static String tablePrefix = "sys_";
}
