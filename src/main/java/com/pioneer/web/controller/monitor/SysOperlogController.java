package com.pioneer.web.controller.monitor;

import cn.hutool.core.map.MapUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysOperLog;
import com.pioneer.web.system.service.ISysOperLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 操作日志记录
 *
 * @author hlm
 * @date 2021-08-09 17:51:40
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {

    @Resource
    private ISysOperLogService operLogService;

    /**
     * 获取操作日志列表
     *
     * @param operLog 查询条件
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:operlog:list')")
    @GetMapping("/list")
    public AjaxResult list(SysOperLog operLog) {
        boolean isPage = startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return isPage ? getDataTable(list) : AjaxResult.success(list);
    }

    /**
     * 导出操作日志
     *
     * @param operLog 查询条件
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPerm('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(SysOperLog operLog) {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        Map<String, String> headAlias = MapUtil.newHashMap(true);
        headAlias.put("title", "操作模块");
        headAlias.put("businessType", "业务类型");
        headAlias.put("method", "请求方法");
        headAlias.put("requestMethod", "请求方式");
        headAlias.put("operatorType", "操作类别");
        headAlias.put("operName", "操作人员");
        headAlias.put("deptName", "部门名称");
        headAlias.put("operUrl", "请求url");
        headAlias.put("operIp", "操作地址");
        headAlias.put("operLocation", "操作地点");
        headAlias.put("operParam", "请求参数");
        headAlias.put("jsonResult", "返回参数");
        headAlias.put("errorMsg", "错误消息");
        headAlias.put("operTime", "操作时间");
        export(list, headAlias);
    }

    /**
     * 删除操作日志
     *
     * @param operIds 日志id数组
     * @return 结果
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPerm('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds) {
        return toAjax(operLogService.removeByIds(Arrays.asList(operIds)));
    }

    /**
     * 清空日志
     *
     * @return 结果
     */
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPerm('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        operLogService.cleanOperLog();
        return AjaxResult.success();
    }
}
