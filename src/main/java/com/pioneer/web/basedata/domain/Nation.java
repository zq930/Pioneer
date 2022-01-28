package com.pioneer.web.basedata.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 民族
 *
 * @author zjf
 * @date 2021-09-25 11:04:00
 */
@Data
@TableName("sys_nation")
public class Nation implements Serializable {

    private static final long serialVersionUID = -4489217948047717385L;

    /**
     * 民族id
     */
    @TableId(type = IdType.AUTO)
    private Long nationId;

    /**
     * 民族
     */
    @TableField(condition = SqlCondition.LIKE)
    private String nationName;
}
