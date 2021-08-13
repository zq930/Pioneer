package com.pioneer.web.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.pioneer.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 通知公告表
 *
 * @author hlm
 * @date 2021-08-09 17:56:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    private static final long serialVersionUID = -8535276756646410455L;

    /**
     * 公告ID
     */
    @TableId(type = IdType.AUTO)
    private Long noticeId;

    /**
     * 公告标题
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;
}
