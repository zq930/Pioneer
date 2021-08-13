package com.pioneer.web.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pioneer.web.system.domain.SysOperLog;
import com.pioneer.web.system.mapper.SysOperLogMapper;
import com.pioneer.web.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SysOperLogServiceImpl
 *
 * @author hlm
 * @date 2021-08-09 18:13:23
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    @Resource
    private SysOperLogMapper operLogMapper;

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        QueryWrapper<SysOperLog> wrapper = new QueryWrapper<>(operLog);
        Object beginTime = operLog.getParams().get("beginTime");
        if (ObjectUtil.isNotNull(beginTime)) {
            wrapper.ge("date_format(oper_time, '%Y-%m-%d')", beginTime);
        }
        Object endTime = operLog.getParams().get("endTime");
        if (ObjectUtil.isNotNull(endTime)) {
            wrapper.le("date_format(oper_time, '%Y-%m-%d')", endTime);
        }
        wrapper.orderByDesc("oper_id");
        return operLogMapper.selectList(wrapper);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
