package com.pioneer.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author hlm
 * @date 2021-08-09 08:57:59
 */
@Component
public class CommonConfig {

    /**
     * 资源名称
     */
    private static String name;

    /**
     * 资源路径
     */
    private static String profile;

    /**
     * 头像路径
     */
    public static final String AVATAR = "/avatar/";

    /**
     * 下载路径
     */
    public static final String DOWNLOAD = "/download/";

    /**
     * 上传路径
     */
    public static final String UPLOAD = "/upload/";

    public static String getName() {
        return name;
    }

    @Value("${common.name}")
    public void setName(String name) {
        CommonConfig.name = name;
    }

    public static String getProfile() {
        return profile;
    }

    @Value("${common.profile}")
    public void setProfile(String profile) {
        CommonConfig.profile = profile;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + AVATAR;
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + DOWNLOAD;
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + UPLOAD;
    }
}
