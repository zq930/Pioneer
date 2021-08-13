package com.pioneer.web.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pioneer.web.system.domain.SysLogininfor;

import java.util.List;

/**
 * ISysLogininforService
 *
 * @author hlm
 * @date 2021-08-09 18:08:03
 */
public interface ISysLogininforService extends IService<SysLogininfor> {

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 清空系统登录日志
     */
    void cleanLogininfor();
}
