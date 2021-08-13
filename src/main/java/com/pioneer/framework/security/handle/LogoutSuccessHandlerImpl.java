package com.pioneer.framework.security.handle;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.framework.manager.AsyncManager;
import com.pioneer.framework.manager.factory.AsyncFactory;
import com.pioneer.framework.web.service.TokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 退出成功处理类
 *
 * @author hlm
 * @date 2021-08-09 17:48:59
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Resource
    private TokenService tokenService;

    /**
     * 退出成功
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (ObjectUtil.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtil.write(response, JSONUtil.toJsonStr(AjaxResult.error(HttpStatus.HTTP_OK, "退出成功")), CharsetUtil.UTF_8);
    }
}
