package com.pioneer.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysNotice;
import com.pioneer.web.system.service.ISysNoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 通知公告
 *
 * @author hlm
 * @date 2021-08-09 17:53:50
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {

    @Resource
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     *
     * @param notice 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:notice:list')")
    @GetMapping("/list")
    public AjaxResult list(SysNotice notice) {
        boolean isPage = startPage();
        List<SysNotice> list = noticeService.list(new QueryWrapper<>(notice));
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 根据通知公告id获取详细信息
     *
     * @param noticeId 通知公告id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        return AjaxResult.success(noticeService.getById(noticeId));
    }

    /**
     * 新增通知公告
     *
     * @param notice 通知公告
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysNotice notice) {
        notice.setCreateBy(getUsername());
        return toAjax(noticeService.save(notice));
    }

    /**
     * 修改通知公告
     *
     * @param notice 通知公告
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysNotice notice) {
        notice.setUpdateBy(getUsername());
        return toAjax(noticeService.updateById(notice));
    }

    /**
     * 删除通知公告
     *
     * @param noticeIds 通知公告id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        return toAjax(noticeService.removeByIds(Arrays.asList(noticeIds)));
    }
}
