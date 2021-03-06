package com.pioneer.web.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pioneer.web.system.domain.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * SysRoleDeptMapper
 *
 * @author hlm
 * @date 2021-08-09 18:04:07
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {

    /**
     * 通过角色ID删除角色和部门关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色部门关联信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteRoleDept(Long[] ids);

    /**
     * 查询部门使用数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 批量新增角色部门信息
     *
     * @param roleDeptList 角色部门列表
     * @return 结果
     */
    int batchRoleDept(List<SysRoleDept> roleDeptList);
}
