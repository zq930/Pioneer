package com.pioneer.web.controller.system;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.constant.UserConstants;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.common.utils.SecurityUtils;
import com.pioneer.common.utils.ServletUtils;
import com.pioneer.framework.web.service.TokenService;
import com.pioneer.web.system.domain.SysRole;
import com.pioneer.web.system.domain.SysUser;
import com.pioneer.web.system.service.ISysPostService;
import com.pioneer.web.system.service.ISysRoleService;
import com.pioneer.web.system.service.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理
 *
 * @author hlm
 * @date 2021-08-09 17:54:33
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysPostService postService;

    @Resource
    private TokenService tokenService;

    /**
     * 获取用户列表
     *
     * @param user 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:list')")
    @GetMapping("/list")
    public AjaxResult list(SysUser user) {
        boolean isPage = startPage();
        List<SysUser> list = userService.selectUserList(user);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出用户信息
     *
     * @param user 查询条件
     * @return 结果
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('system:user:export')")
    @GetMapping("/export")
    public AjaxResult export(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("userId", "用户ID");
        headAlias.put("userName", "用户账号");
        headAlias.put("nickName", "用户昵称");
        headAlias.put("email", "用户邮箱");
        headAlias.put("phoneNumber", "手机号码");
        headAlias.put("sex", "用户性别");
        headAlias.put("loginIp", "最后登录IP");
        headAlias.put("loginDate", "最后登录时间");
        return export(list, headAlias);
    }

    /**
     * 导入用户信息
     *
     * @param file          导入文件
     * @param updateSupport 是否更新支持，如果已存在，则进行更新数据
     * @return 结果
     * @throws Exception 异常
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPerm('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        List<SysUser> userList = ExcelUtil.getReader(file.getInputStream()).readAll(SysUser.class);
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 下载导入模板
     *
     * @return 结果
     */
    @GetMapping("/importTemplate")
    public AjaxResult importTemplate() {
        return AjaxResult.success("用户导入模板.xlsx");
    }

    /**
     * 根据用户id获取详细信息
     *
     * @param userId 用户id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.list());
        if (ObjectUtil.isNotNull(userId)) {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StrUtil.isNotEmpty(user.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StrUtil.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if (StrUtil.isNotEmpty(user.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StrUtil.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     *
     * @param userIds 用户id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     *
     * @param user 用户信息（新密码）
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     *
     * @param user 用户信息(状态)
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户id
     * @param roleIds 角色id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        userService.insertUserAuth(userId, roleIds);
        return success();
    }
}
