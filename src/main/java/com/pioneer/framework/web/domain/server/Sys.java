package com.pioneer.framework.web.domain.server;

import lombok.Getter;
import lombok.Setter;

/**
 * Sys
 *
 * @author hlm
 * @date 2021-08-09 17:49:56
 */
@Getter
@Setter
public class Sys {

    /**
     * 服务器名称
     */
    private String computerName;

    /**
     * 服务器Ip
     */
    private String computerIp;

    /**
     * 项目路径
     */
    private String userDir;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;
}
