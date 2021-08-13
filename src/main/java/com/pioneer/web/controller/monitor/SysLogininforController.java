package com.pioneer.web.controller.monitor;

import cn.hutool.core.map.MapUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysLogininfor;
import com.pioneer.web.system.service.ISysLogininforService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 系统访问记录
 *
 * @author hlm
 * @date 2021-08-09 17:51:32
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    @Resource
    private ISysLogininforService logininforService;

    /**
     * 获取登录日志列表
     *
     * @param logininfor 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:logininfor:list')")
    @GetMapping("/list")
    public AjaxResult list(SysLogininfor logininfor) {
        boolean isPage = startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出登录日志
     *
     * @param logininfor 查询条件
     * @return 结果
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('monitor:logininfor:export')")
    @GetMapping("/export")
    public AjaxResult export(SysLogininfor logininfor) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("userName", "用户账号");
        headAlias.put("status", "登录状态");
        headAlias.put("ipaddr", "登录IP地址");
        headAlias.put("loginLocation", "登陆地点");
        headAlias.put("browser", "浏览器类型");
        headAlias.put("os", "操作系统");
        headAlias.put("msg", "提示消息");
        headAlias.put("loginTime", "访问时间");
        return export(list, headAlias);
    }

    /**
     * 删除登录日志
     *
     * @param infoIds 日志id数组
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.removeByIds(Arrays.asList(infoIds)));
    }

    /**
     * 清空日志
     *
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        logininforService.cleanLogininfor();
        return AjaxResult.success();
    }
}
