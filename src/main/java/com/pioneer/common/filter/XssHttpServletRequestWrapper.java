package com.pioneer.common.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * XSS过滤处理
 *
 * @author hlm
 * @date 2021-08-09 17:45:53
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapeValues = new String[length];
            for (int i = 0; i < length; i++) {
                // 防xss攻击和过滤前后空格
                escapeValues[i] = HtmlUtil.filter(values[i]).trim();
            }
            return escapeValues;
        }
        return super.getParameterValues(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 非json类型，直接返回
        if (!isJsonRequest()) {
            return super.getInputStream();
        }

        // 为空，直接返回
        String json = IoUtil.readUtf8(super.getInputStream());
        if (StrUtil.isEmpty(json)) {
            return super.getInputStream();
        }

        // xss过滤
        json = HtmlUtil.filter(json).trim();
        final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes(CharsetUtil.CHARSET_UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return bis.read();
            }
        };
    }

    /**
     * 是否是Json请求
     */
    public boolean isJsonRequest() {
        String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return StrUtil.startWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE);
    }
}
