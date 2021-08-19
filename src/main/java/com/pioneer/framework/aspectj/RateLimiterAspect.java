package com.pioneer.framework.aspectj;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.pioneer.common.annotation.RateLimiter;
import com.pioneer.common.enums.LimitType;
import com.pioneer.common.exception.CustomException;
import com.pioneer.common.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流处理
 *
 * @author hlm
 * @date 2021-08-19 08:36:08
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    private RedisTemplate<Object, Object> redisTemplate;

    private RedisScript<Long> limitScript;

    @Resource
    public void setRedisTemplate1(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Resource
    public void setLimitScript(RedisScript<Long> limitScript) {
        this.limitScript = limitScript;
    }

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.pioneer.common.annotation.RateLimiter)")
    public void rateLimiterPointCut() {
    }

    @Before("rateLimiterPointCut()")
    public void doBefore(JoinPoint point) {
        RateLimiter rateLimiter = getAnnotationRateLimiter(point);
        assert rateLimiter != null;
        String key = rateLimiter.key();
        int time = rateLimiter.time();
        int count = rateLimiter.count();

        String combineKey = getCombineKey(rateLimiter, point);
        List<Object> keys = Collections.singletonList(combineKey);
        try {
            Long number = redisTemplate.execute(limitScript, keys, count, time);
            if (ObjectUtil.isNull(number) || number.intValue() > count) {
                throw new CustomException("访问过于频繁，请稍后再试");
            }
            log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), key);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("服务器限流异常，请稍后再试");
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private RateLimiter getAnnotationRateLimiter(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(RateLimiter.class);
        }
        return null;
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        StringBuilder stringBuffer = new StringBuilder(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            stringBuffer.append(ServletUtil.getClientIP(ServletUtils.getRequest()));
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append("-").append(targetClass.getName()).append("- ").append(method.getName());
        return stringBuffer.toString();
    }
}
