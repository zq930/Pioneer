package com.pioneer.web.basedata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pioneer.web.basedata.domain.Nation;
import com.pioneer.web.basedata.mapper.NationMapper;
import com.pioneer.web.basedata.service.INationService;
import org.springframework.stereotype.Service;

/**
 * 民族Service业务层处理
 *
 * @author zjf
 * @date 2021-09-25 11:04:00
 */
@Service
public class NationServiceImpl extends ServiceImpl<NationMapper, Nation> implements INationService {

}
