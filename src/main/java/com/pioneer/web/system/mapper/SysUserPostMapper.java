package com.pioneer.web.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pioneer.web.system.domain.SysUserPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * SysUserPostMapper
 *
 * @author hlm
 * @date 2021-08-09 18:04:48
 */
@Mapper
public interface SysUserPostMapper extends BaseMapper<SysUserPost> {

    /**
     * 通过用户ID删除用户和岗位关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserPostByUserId(Long userId);

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    int countUserPostById(Long postId);

    /**
     * 批量删除用户和岗位关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUserPost(Long[] ids);

    /**
     * 批量新增用户岗位信息
     *
     * @param userPostList 用户角色列表
     * @return 结果
     */
    int batchUserPost(List<SysUserPost> userPostList);
}
