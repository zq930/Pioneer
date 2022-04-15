package com.pioneer.web.controller.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysDictData;
import com.pioneer.web.system.service.ISysDictDataService;
import com.pioneer.web.system.service.ISysDictTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字典数据
 *
 * @author hlm
 * @date 2021-08-09 17:52:11
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    @Resource
    private ISysDictDataService dictDataService;

    @Resource
    private ISysDictTypeService dictTypeService;

    /**
     * 获取字典数据列表
     *
     * @param dictData 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dict:list')")
    @GetMapping("/list")
    public AjaxResult list(SysDictData dictData) {
        boolean isPage = startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出字典数据
     *
     * @param dictData 查询条件
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('system:dict:export')")
    @PostMapping("/export")
    public void export(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("dictCode", "字典编码");
        headAlias.put("dictSort", "字典排序");
        headAlias.put("dictLabel", "字典标签");
        headAlias.put("dictValue", "字典键值");
        headAlias.put("dictType", "字典类型");
        export(list, headAlias);
    }

    /**
     * 查询字典数据详细
     *
     * @param dictCode 字典编码
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode) {
        return AjaxResult.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 结果
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType) {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (CollUtil.isEmpty(data)) {
            data = new ArrayList<>();
        }
        return AjaxResult.success(data);
    }

    /**
     * 新增字典数据
     *
     * @param dict 字典数据信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict) {
        dict.setCreateBy(getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     *
     * @param dict 字典数据信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict) {
        dict.setUpdateBy(getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     *
     * @param dictCodes 字典数据id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }
}
