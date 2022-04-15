package com.pioneer.web.controller.monitor;

import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.framework.web.domain.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author hlm
 * @date 2021-08-09 17:51:26
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    /**
     * 获取服务器监控信息
     *
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:server:list')")
    @GetMapping()
    public AjaxResult getInfo() {
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }
}
