package com.pioneer.web.controller.system;

import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.LoginBody;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.utils.SecurityUtils;
import com.pioneer.common.utils.ServletUtils;
import com.pioneer.framework.web.service.SysLoginService;
import com.pioneer.framework.web.service.SysPermissionService;
import com.pioneer.framework.web.service.TokenService;
import com.pioneer.web.system.domain.SysMenu;
import com.pioneer.web.system.domain.SysUser;
import com.pioneer.web.system.service.ISysMenuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 登录
 *
 * @author hlm
 * @date 2021-08-09 17:53:13
 */
@RestController
public class SysLoginController {

    @Resource
    private SysLoginService loginService;

    @Resource
    private ISysMenuService menuService;

    @Resource
    private SysPermissionService permissionService;

    @Resource
    private TokenService tokenService;

    /**
     * 登录
     *
     * @param loginBody 用户登录对象
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@Validated @RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(SecurityUtils.getUserId());
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
