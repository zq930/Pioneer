package com.pioneer.web.system.domain;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录表
 *
 * @author hlm
 * @date 2021-08-09 17:57:01
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 7303420550813997408L;

    /**
     * 操作日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long operId;

    /**
     * 操作模块
     */
    @TableField(condition = SqlCondition.LIKE)
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 业务类型数组
     */
    private transient Integer[] businessTypes;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    @TableField(condition = SqlCondition.LIKE)
    private String operName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 请求url
     */
    private String operUrl;

    /**
     * 操作地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 返回参数
     */
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private Date operTime;

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
