package com.pioneer.web.controller.system;

import cn.hutool.core.map.MapUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.annotation.RepeatSubmit;
import com.pioneer.common.constant.UserConstants;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysConfig;
import com.pioneer.web.system.service.ISysConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 参数配置
 *
 * @author hlm
 * @date 2021-08-09 17:51:58
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {

    @Resource
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     *
     * @param config 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:config:list')")
    @GetMapping("/list")
    public AjaxResult list(SysConfig config) {
        boolean isPage = startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出参数配置
     *
     * @param config 查询条件
     * @return 结果
     */
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('system:config:export')")
    @PostMapping("/export")
    public void export(SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("configId", "参数ID");
        headAlias.put("configName", "参数名称");
        headAlias.put("configKey", "参数键值");
        headAlias.put("configValue", "参数名称");
        export(list, headAlias);
    }

    /**
     * 根据参数id获取详细信息
     *
     * @param configId 参数id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId) {
        return AjaxResult.success(configService.getById(configId));
    }

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数键名
     * @return 结果
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey) {
        return AjaxResult.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    public AjaxResult add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return AjaxResult.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return AjaxResult.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     *
     * @param configIds 参数id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存
     *
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache() {
        configService.resetConfigCache();
        return AjaxResult.success();
    }
}
