package com.pioneer.web.controller.system;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.constant.UserConstants;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysDept;
import com.pioneer.web.system.service.ISysDeptService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门管理
 *
 * @author hlm
 * @date 2021-08-09 17:52:04
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Resource
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     *
     * @param dept 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return AjaxResult.success(deptList);
    }

    /**
     * 查询部门列表（排除节点）
     *
     * @param deptId 部门id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        deptList.removeIf(d -> d.getDeptId().intValue() == deptId
                || ArrayUtil.contains(d.getAncestors().split(StrUtil.COMMA), deptId + StrUtil.EMPTY));
        return AjaxResult.success(deptList);
    }

    /**
     * 根据部门id获取详细信息
     *
     * @param deptId 部门id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     *
     * @param dept 查询条件
     * @return 结果
     */
    @GetMapping("/treeSelect")
    public AjaxResult treeSelect(SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.buildDeptTreeSelect(deptList));
    }

    /**
     * 加载对应角色部门列表树
     *
     * @param roleId 角色id
     * @return 结果
     */
    @GetMapping(value = "/roleDeptTreeSelect/{roleId}")
    public AjaxResult roleDeptTreeSelect(@PathVariable("roleId") Long roleId) {
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("deptList", deptService.buildDeptTreeSelect(deptList));
        return ajax;
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return AjaxResult.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDept dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StrUtil.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0) {
            return AjaxResult.error("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     *
     * @param deptId 部门id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return AjaxResult.error("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return AjaxResult.error("部门存在用户,不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }
}
