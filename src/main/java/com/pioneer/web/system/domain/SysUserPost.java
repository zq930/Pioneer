package com.pioneer.web.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户岗位关联表
 *
 * @author hlm
 * @date 2021-08-09 17:59:44
 */
@Data
@TableName("sys_user_post")
public class SysUserPost implements Serializable {

    private static final long serialVersionUID = 2523216308376300542L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postId;
}
