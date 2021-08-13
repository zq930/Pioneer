package com.pioneer.web.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.pioneer.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 参数配置表
 *
 * @author hlm
 * @date 2021-08-09 17:55:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    private static final long serialVersionUID = 4496391906583789260L;

    /**
     * 参数配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long configId;

    /**
     * 参数名称
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    private String configName;

    /**
     * 参数键名
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "参数键名长度不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;
}
