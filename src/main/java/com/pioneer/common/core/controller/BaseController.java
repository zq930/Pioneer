package com.pioneer.common.core.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pioneer.common.config.CommonConfig;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.utils.SecurityUtils;
import com.pioneer.common.utils.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * web层通用数据处理
 *
 * @author hlm
 * @date 2021-08-09 08:48:42
 */
public class BaseController {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY = "orderByColumn";

    /**
     * 排序列
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 排序类型：ASC
     */
    public static final String ASC = "ascending";

    /**
     * 排序类型：DESC
     */
    public static final String DESC = "descending";

    /**
     * 设置请求分页数据
     */
    protected boolean startPage() {
        // 请求参数同时含有pageNum和pageSize时分页才会生效
        HttpServletRequest request = ServletUtils.getRequest();
        Integer pageNum = Convert.toInt(request.getParameter(PAGE_NUM));
        Integer pageSize = Convert.toInt(request.getParameter(PAGE_SIZE));
        if (ObjectUtil.isNull(pageNum) || ObjectUtil.isNull(pageSize)) {
            return false;
        }
        // 调用PageHelper分页插件功能
        PageHelper.startPage(pageNum, pageSize);
        // 排序字段
        String orderBy = request.getParameter(ORDER_BY);
        if (StrUtil.isNotBlank(orderBy)) {
            // 驼峰转下划线
            orderBy = StrUtil.toUnderlineCase(orderBy);
            // 排序类型
            String isAsc = request.getParameter(IS_ASC);
            if (StrUtil.isNotEmpty(isAsc)) {
                // 兼容前端排序类型
                if (ASC.equals(isAsc)) {
                    isAsc = "asc";
                } else if (DESC.equals(isAsc)) {
                    isAsc = "desc";
                }
                orderBy += " " + isAsc;
            }
            PageHelper.orderBy(orderBy);
        }
        return true;
    }

    /**
     * 响应请求分页数据
     */
    protected AjaxResult getDataTable(List<?> list) {
        AjaxResult result = AjaxResult.success("查询成功");
        result.put("rows", list);
        result.put("total", new PageInfo<>(list).getTotal());
        return result;
    }

    /**
     * 返回成功
     */
    public AjaxResult success() {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 获取用户缓存信息
     */
    public LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取登录用户id
     */
    public Long getUserId() {
        return getLoginUser().getUserId();
    }

    /**
     * 获取登录部门id
     */
    public Long getDeptId() {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取登录用户名
     */
    public String getUsername() {
        return getLoginUser().getUsername();
    }

    /**
     * 导出
     *
     * @param list      导出结果集
     * @param headAlias 导出表头
     */
    protected AjaxResult export(Collection<?> list, Map<String, String> headAlias) {
        String filename = IdUtil.getSnowflake().nextId() + ".xlsx";
        ExcelWriter writer = ExcelUtil.getWriter(CommonConfig.getDownloadPath() + filename);
        writer.setHeaderAlias(headAlias);
        writer.write(list, true);
        writer.close();
        return AjaxResult.success(filename);
    }
}
