package com.pioneer.common.annotation;

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author hlm
 * @date 2021-08-09 08:39:29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     */
    String deptAlias() default StrUtil.EMPTY;

    /**
     * 用户表的别名
     */
    String userAlias() default StrUtil.EMPTY;
}
