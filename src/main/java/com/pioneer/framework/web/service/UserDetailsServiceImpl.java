package com.pioneer.framework.web.service;

import cn.hutool.core.util.ObjectUtil;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.enums.UserStatus;
import com.pioneer.common.exception.CustomException;
import com.pioneer.web.system.domain.SysUser;
import com.pioneer.web.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户验证处理
 *
 * @author hlm
 * @date 2021-08-09 17:50:42
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private ISysUserService userService;

    @Resource
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new CustomException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new CustomException("对不起，您的账号：" + username + " 已停用");
        }
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
