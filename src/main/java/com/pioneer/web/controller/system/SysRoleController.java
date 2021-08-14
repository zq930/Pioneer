package com.pioneer.web.controller.system;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.constant.UserConstants;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.common.utils.ServletUtils;
import com.pioneer.framework.web.service.SysPermissionService;
import com.pioneer.framework.web.service.TokenService;
import com.pioneer.web.system.domain.SysRole;
import com.pioneer.web.system.domain.SysUser;
import com.pioneer.web.system.domain.SysUserRole;
import com.pioneer.web.system.service.ISysRoleService;
import com.pioneer.web.system.service.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author hlm
 * @date 2021-08-09 17:54:25
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Resource
    private ISysRoleService roleService;

    @Resource
    private TokenService tokenService;

    @Resource
    private SysPermissionService permissionService;

    @Resource
    private ISysUserService userService;

    /**
     * 角色列表
     *
     * @param role 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:list')")
    @GetMapping("/list")
    public AjaxResult list(SysRole role) {
        boolean isPage = startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出角色管理
     *
     * @param role 查询条件
     * @return 结果
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('system:role:export')")
    @GetMapping("/export")
    public AjaxResult export(SysRole role) {
        List<SysRole> list = roleService.selectRoleList(role);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("roleId", "角色ID");
        headAlias.put("roleName", "角色名称");
        headAlias.put("roleKey", "角色权限");
        headAlias.put("roleSort", "角色排序");
        return export(list, headAlias);
    }

    /**
     * 根据角色id获取详细信息
     *
     * @param roleId 角色id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId) {
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     *
     * @param role 角色信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(getUsername());

        if (roleService.updateRole(role) > 0) {
            // 更新缓存用户权限
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            if (ObjectUtil.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin()) {
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                tokenService.setLoginUser(loginUser);
            }
            return AjaxResult.success();
        }
        return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     *
     * @param role 数据权限
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     *
     * @param role 角色信息（状态）
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     *
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:query')")
    @GetMapping("/optionSelect")
    public AjaxResult optionSelect() {
        return AjaxResult.success(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public AjaxResult allocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * 查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public AjaxResult unallocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 取消授权用户
     *
     * @param userRole 用户角色
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole) {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     *
     * @param roleId  角色id
     * @param userIds 用户id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     *
     * @param roleId  角色id
     * @param userIds 用户id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }
}
