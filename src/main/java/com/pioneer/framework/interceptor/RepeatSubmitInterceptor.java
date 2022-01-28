package com.pioneer.framework.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pioneer.common.annotation.RepeatSubmit;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.redis.RedisCache;
import com.pioneer.common.filter.RepeatedlyRequestWrapper;
import com.pioneer.common.utils.ServletUtils;
import com.pioneer.framework.web.service.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交拦截器
 *
 * @author hlm
 * @date 2021-08-09 17:48:05
 */
@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    @Resource
    private RedisCache redisCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(request, annotation)) {
                    AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                    ServletUtils.write(response, JSONUtil.toJsonStr(ajaxResult), CharsetUtil.UTF_8);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request 请求
     * @return boolean
     */
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
        String nowParams = StrUtil.EMPTY;
        if (request instanceof RepeatedlyRequestWrapper) {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
            nowParams = ServletUtils.getBody(repeatedlyRequest);
        }

        // body参数为空，获取Parameter的数据
        if (StrUtil.isEmpty(nowParams)) {
            nowParams = JSONUtil.toJsonStr(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<>(4);
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 唯一值（没有消息头则使用请求地址）
        String submitKey = StrUtil.trimToEmpty(request.getHeader(TokenService.HEADER));

        // 唯一标识（指定key + url + 消息头）
        String cacheRepeatKey = Constants.REPEAT_SUBMIT_KEY + url + submitKey;

        Object sessionObj = redisCache.getCacheObject(cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = Convert.toMap(String.class, Object.class, sessionObj);
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = Convert.toMap(String.class, Object.class, sessionMap.get(url));
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval())) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<>(16);
        cacheMap.put(url, nowDataMap);
        redisCache.setCacheObject(cacheRepeatKey, cacheMap, annotation.interval(), TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < interval;
    }
}
