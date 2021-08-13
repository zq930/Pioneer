package com.pioneer.web.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色部门关联表
 *
 * @author hlm
 * @date 2021-08-09 17:57:59
 */
@Data
@TableName("sys_role_dept")
public class SysRoleDept implements Serializable {

    private static final long serialVersionUID = -1578244970848265184L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 部门ID
     */
    private Long deptId;
}
