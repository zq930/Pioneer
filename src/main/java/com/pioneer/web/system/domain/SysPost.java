package com.pioneer.web.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.pioneer.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 岗位表
 *
 * @author hlm
 * @date 2021-08-09 17:57:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_post")
public class SysPost extends BaseEntity {

    private static final long serialVersionUID = 7692928134321595761L;

    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Long postId;

    /**
     * 岗位编码
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码长度不能超过64个字符")
    private String postCode;

    /**
     * 岗位名称
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称长度不能超过50个字符")
    private String postName;

    /**
     * 岗位排序
     */
    @NotBlank(message = "显示顺序不能为空")
    private String postSort;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 用户是否存在此岗位标识 默认不存在
     */
    private transient boolean flag = false;
}
