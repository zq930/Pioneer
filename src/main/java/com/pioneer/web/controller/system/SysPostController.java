package com.pioneer.web.controller.system;

import cn.hutool.core.map.MapUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.constant.UserConstants;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysPost;
import com.pioneer.web.system.service.ISysPostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 岗位管理
 *
 * @author hlm
 * @date 2021-08-09 17:53:59
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    @Resource
    private ISysPostService postService;

    /**
     * 获取岗位列表
     *
     * @param post 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:post:list')")
    @GetMapping("/list")
    public AjaxResult list(SysPost post) {
        boolean isPage = startPage();
        List<SysPost> list = postService.selectPostList(post);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出岗位信息
     *
     * @param post 查询条件
     * @return 结果
     */
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('system:post:export')")
    @PostMapping("/export")
    public void export(SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("postId", "岗位ID");
        headAlias.put("postCode", "岗位编码");
        headAlias.put("postName", "岗位名称");
        headAlias.put("postSort", "岗位排序");
        export(list, headAlias);
    }

    /**
     * 根据岗位id获取详细信息
     *
     * @param postId 岗位id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId) {
        return AjaxResult.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     *
     * @param post 岗位信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     *
     * @param post 岗位信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     *
     * @param postIds 岗位id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds) {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionSelect")
    public AjaxResult optionSelect() {
        List<SysPost> posts = postService.selectPostAll();
        return AjaxResult.success(posts);
    }
}
