package com.pioneer.common.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Method;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * XssFilter
 *
 * @author hlm
 * @date 2021-08-09 17:45:47
 */
public class XssFilter implements Filter {

    /**
     * 排除链接
     */
    public final List<String> excludes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        if (StrUtil.isNotEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(StrUtil.COMMA);
            Collections.addAll(excludes, url);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (handleExcludeUrl(req)) {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeUrl(HttpServletRequest request) {
        String method = request.getMethod();
        // GET DELETE 不过滤
        if (method == null || method.matches(Method.GET.name()) || method.matches(Method.DELETE.name())) {
            return true;
        }
        String url = request.getServletPath();
        if (StrUtil.isEmpty(url) || CollUtil.isEmpty(excludes)) {
            return false;
        }
        for (String pattern : excludes) {
            return new AntPathMatcher().match(pattern, url);
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
