package com.pioneer.framework.web.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 自定义权限实现
 *
 * @author hlm
 * @date 2021-08-09 17:50:06
 */
@Service("ss")
public class PermissionService {

    /**
     * 所有权限标识
     */
    public static final String ALL_PERMISSION = "*:*:*";

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPerm(String permission) {
        if (StrUtil.isEmpty(permission)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (ObjectUtil.isNull(loginUser)) {
            return false;
        }
        // 用户权限集合
        Set<String> permissions = loginUser.getPermissions();
        if (CollUtil.isEmpty(permissions)) {
            return false;
        }
        // 判断是否包含指定权限
        return permissions.contains(ALL_PERMISSION) || permissions.contains(permission.trim());
    }
}
