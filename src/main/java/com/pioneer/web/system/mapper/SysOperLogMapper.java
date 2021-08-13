package com.pioneer.web.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pioneer.web.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysOperLogMapper
 *
 * @author hlm
 * @date 2021-08-09 18:03:48
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
