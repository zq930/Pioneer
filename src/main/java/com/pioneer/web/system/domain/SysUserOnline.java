package com.pioneer.web.system.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 在线用户
 *
 * @author hlm
 * @date 2021-08-09 17:59:12
 */
@Data
public class SysUserOnline implements Serializable {

    private static final long serialVersionUID = 1520692505403552887L;

    /**
     * 会话ID
     */
    private String tokenId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地址
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录时间
     */
    private Long loginTime;
}
