package com.pioneer.framework.security.handle;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.pioneer.common.core.domain.AjaxResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证失败处理类
 *
 * @author hlm
 * @date 2021-08-09 17:48:52
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        int code = HttpStatus.HTTP_UNAUTHORIZED;
        String msg = StrUtil.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        ServletUtil.write(response, JSONUtil.toJsonStr(AjaxResult.error(code, msg)), CharsetUtil.UTF_8);
    }
}
