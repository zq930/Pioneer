package com.pioneer.web.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录日志表
 *
 * @author hlm
 * @date 2021-08-09 17:56:28
 */
@Data
@TableName("sys_logininfor")
public class SysLogininfor implements Serializable {

    private static final long serialVersionUID = -5095845395855884541L;

    /**
     * 登录日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long infoId;

    /**
     * 用户账号
     */
    @TableField(condition = SqlCondition.LIKE)
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * 登录IP地址
     */
    @TableField(condition = SqlCondition.LIKE)
    private String ipaddr;

    /**
     * 登录地点
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
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private LocalDateTime loginTime;

    /**
     * 请求参数
     */
    private transient Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>(16);
        }
        return params;
    }
}
