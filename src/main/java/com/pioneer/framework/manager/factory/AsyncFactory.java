package com.pioneer.framework.manager.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.utils.AddressUtils;
import com.pioneer.common.utils.ServletUtils;
import com.pioneer.web.system.domain.SysLogininfor;
import com.pioneer.web.system.domain.SysOperLog;
import com.pioneer.web.system.service.ISysLogininforService;
import com.pioneer.web.system.service.ISysOperLogService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.TimerTask;

/**
 * 异步工厂
 *
 * @author hlm
 * @date 2021-08-09 17:48:16
 */
@Slf4j
public class AsyncFactory {

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message, final Object... args) {
        final UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = ServletUtil.getClientIP(ServletUtils.getRequest());
        return new TimerTask() {
            @Override
            public void run() {
                // 查询地址
                String address = AddressUtils.getAddressByIp(ip);
                // 打印信息到日志
                String template = "[{}]";
                String s = StrUtil.format(template, ip) + address +
                        StrUtil.format(template, username) +
                        StrUtil.format(template, status) +
                        StrUtil.format(template, message);
                log.info(s, args);
                // 获取客户端操作系统
                String os = userAgent.getPlatform() + StrUtil.SPACE + userAgent.getOsVersion();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser() + StrUtil.SPACE + userAgent.getVersion();
                // 封装对象
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setUserName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                logininfor.setLoginTime(LocalDateTime.now());
                // 日志状态
                if (StrUtil.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
                    logininfor.setStatus(Constants.SUCCESS);
                } else if (Constants.LOGIN_FAIL.equals(status)) {
                    logininfor.setStatus(Constants.FAIL);
                }
                // 插入数据
                SpringUtil.getBean(ISysLogininforService.class).save(logininfor);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getAddressByIp(operLog.getOperIp()));
                operLog.setOperTime(LocalDateTime.now());
                SpringUtil.getBean(ISysOperLogService.class).save(operLog);
            }
        };
    }
}
