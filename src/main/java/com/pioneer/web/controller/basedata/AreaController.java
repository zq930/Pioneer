package com.pioneer.web.controller.basedata;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.basedata.domain.Area;
import com.pioneer.web.basedata.service.IAreaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 地区
 *
 * @author zjf
 * @date 2021-09-24 16:14:43
 */
@RestController
@RequestMapping("/basedata/area")
public class AreaController extends BaseController {

    @Resource
    private IAreaService areaService;


    /**
     * 获取地区列表
     *
     * @param area 查询条件
     * @return 结果
     *
     */
    @GetMapping("/list")
    public AjaxResult list(Area area) {
        List<Area> list = areaService.list(Wrappers.query(area));
        return AjaxResult.success(list);
    }


    /**
     * 获取地区列表(懒加载)
     *
     * @param area 查询条件
     * @return 结果
     *
     */
    @PreAuthorize("@ss.hasPerm('basedata:area:list')")
    @GetMapping("/treeList")
    public AjaxResult treeList(Area area) {
        List<Area> list = areaService.list(Wrappers.query(area));
        //懒加载封装hasChildren属性
        for (Area area1 : list) {
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.eq("pid",area1.getId());
            long count = areaService.count(areaQueryWrapper);
            if(count>0){
                area1.setHasChildren(true);
            }
        }
        return AjaxResult.success(list);
    }

    /**
     * 根据地区id获取详细信息
     *
     * @param id 地区id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:area:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(areaService.getById(id));
    }

    /**
     * 新增地区
     *
     * @param area 地区
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:area:add')")
    @Log(title = "地区", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Area area) {
        return toAjax(areaService.save(area));
    }

    /**
     * 修改地区
     *
     * @param area 地区
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:area:edit')")
    @Log(title = "地区", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Area area) {
        return toAjax(areaService.updateById(area));
    }

    /**
     * 删除地区
     *
     * @param ids 地区id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('basedata:area:remove')")
    @Log(title = "地区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
        public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(areaService.removeByIds(Arrays.asList(ids)));
    }
}
