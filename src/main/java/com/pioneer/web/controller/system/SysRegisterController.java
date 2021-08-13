package com.pioneer.web.controller.system;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.RegisterBody;
import com.pioneer.framework.web.service.SysRegisterService;
import com.pioneer.web.system.service.ISysConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 注册
 *
 * @author hlm
 * @date 2021-08-09 17:54:16
 */
@RestController
public class SysRegisterController extends BaseController {

    @Resource
    private SysRegisterService registerService;

    @Resource
    private ISysConfigService configService;

    /**
     * 注册
     *
     * @param user 注册信息
     * @return 结果
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user) {
        String key = "sys.account.registerUser";
        String config = configService.selectConfigByKey(key);
        if (!BooleanUtil.toBoolean(config)) {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StrUtil.isEmpty(msg) ? success() : error(msg);
    }
}
