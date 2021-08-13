package com.pioneer.web.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pioneer.web.system.domain.SysOperLog;

import java.util.List;

/**
 * ISysOperLogService
 *
 * @author hlm
 * @date 2021-08-09 18:08:48
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
