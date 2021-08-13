package com.pioneer.web.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pioneer.web.system.domain.SysLogininfor;
import com.pioneer.web.system.mapper.SysLogininforMapper;
import com.pioneer.web.system.service.ISysLogininforService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SysLogininforServiceImpl
 *
 * @author hlm
 * @date 2021-08-09 18:13:12
 */
@Service
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforService {

    @Resource
    private SysLogininforMapper logininforMapper;

    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor loginInfo) {
        QueryWrapper<SysLogininfor> wrapper = new QueryWrapper<>(loginInfo);
        Object beginTime = loginInfo.getParams().get("beginTime");
        if (ObjectUtil.isNotNull(beginTime)) {
            wrapper.ge("date_format(login_time, '%Y-%m-%d')", beginTime);
        }
        Object endTime = loginInfo.getParams().get("endTime");
        if (ObjectUtil.isNotNull(endTime)) {
            wrapper.le("date_format(login_time, '%Y-%m-%d')", endTime);
        }
        wrapper.orderByDesc("info_id");
        return logininforMapper.selectList(wrapper);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
