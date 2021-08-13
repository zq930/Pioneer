package com.pioneer.web.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pioneer.web.system.domain.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysLogininforMapper
 *
 * @author hlm
 * @date 2021-08-09 18:03:21
 */
@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {

    /**
     * 清空系统登录日志
     *
     * @return 结果
     */
    int cleanLogininfor();
}
