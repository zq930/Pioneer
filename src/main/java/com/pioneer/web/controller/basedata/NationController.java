package com.pioneer.web.controller.basedata;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.pioneer.common.annotation.Log;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.basedata.domain.Nation;
import com.pioneer.web.basedata.service.INationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 民族
 *
 * @author zjf
 * @date 2021-09-25 11:04:00
 */
@RestController
@RequestMapping("/basedata/nation")
public class NationController extends BaseController {

    @Resource
    private INationService nationService;

    /**
     * 获取民族列表
     *
     * @param nation 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:nation:list')")
    @GetMapping("/list")
        public AjaxResult list(Nation nation) {
        boolean isPage = startPage();
        List<Nation> list = nationService.list(Wrappers.query(nation));
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 根据民族id获取详细信息
     *
     * @param nationId 民族id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:nation:query')")
    @GetMapping(value = "/{nationId}")
    public AjaxResult getInfo(@PathVariable Long nationId) {
        return AjaxResult.success(nationService.getById(nationId));
    }

    /**
     * 新增民族
     *
     * @param nation 民族
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:nation:add')")
    @Log(title = "民族", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Nation nation) {
        return toAjax(nationService.save(nation));
    }

    /**
     * 修改民族
     *
     * @param nation 民族
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:nation:edit')")
    @Log(title = "民族", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Nation nation) {
        return toAjax(nationService.updateById(nation));
    }

    /**
     * 删除民族
     *
     * @param nationIds 民族id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:nation:remove')")
    @Log(title = "民族", businessType = BusinessType.DELETE)
    @DeleteMapping("/{nationIds}")
        public AjaxResult remove(@PathVariable Long[] nationIds) {
        return toAjax(nationService.removeByIds(Arrays.asList(nationIds)));
    }
}
