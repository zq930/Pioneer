package com.pioneer.common.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解防止表单重复提交
 *
 * @author hlm
 * @date 2021-08-09 08:45:59
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

}
