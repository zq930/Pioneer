package com.pioneer.web.controller.system;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author hlm
 * @date 2021-08-09 17:52:34
 */
@RestController
public class SysIndexController {

    /**
     * 首页
     *
     * @return 结果
     */
    @RequestMapping("/")
    public String index() {
        return StrUtil.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", "xx系统", "1.0");
    }
}
