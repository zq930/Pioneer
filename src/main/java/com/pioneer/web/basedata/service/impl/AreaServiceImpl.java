package com.pioneer.web.basedata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pioneer.web.basedata.domain.Area;
import com.pioneer.web.basedata.mapper.AreaMapper;
import com.pioneer.web.basedata.service.IAreaService;
import org.springframework.stereotype.Service;

/**
 * 地区Service业务层处理
 *
 * @author zjf
 * @date 2021-09-24 16:14:43
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

}
