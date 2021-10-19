package com.pioneer.web.basedata.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 地区
 *
 * @author zjf
 * @date 2021-09-24 16:14:43
 */
@Data
@TableName("sys_area")
public class Area {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父id
     */
    private Long pid;

    /**
     * 简称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String shortname;

    /**
     * 名称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    /**
     * 全称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String mergerName;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 长途区号
     */
    private String code;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 首字母
     */
    private String first;

    /**
     * 经度
     */
    private String lng;

    /**
     * 纬度
     */
    private String lat;

    @TableField(exist = false)
    private boolean hasChildren;
}
