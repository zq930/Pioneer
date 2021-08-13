package com.pioneer.generator.util;

import cn.hutool.core.util.CharsetUtil;
import org.apache.velocity.app.Velocity;

import java.util.Properties;

/**
 * VelocityInitializer
 *
 * @author hlm
 * @date 2021-08-12 12:45:11
 */
public class VelocityInitializer {

    /**
     * 初始化vm方法
     */
    public static void initVelocity() {
        Properties p = new Properties();
        try {
            // 加载classpath目录下的vm文件
            p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, CharsetUtil.UTF_8);
            p.setProperty(Velocity.OUTPUT_ENCODING, CharsetUtil.UTF_8);
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
