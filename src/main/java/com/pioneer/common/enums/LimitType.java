package com.pioneer.common.enums;

/**
 * 限流类型
 *
 * @author hlm
 * @date 2021-08-19 08:33:29
 */
public enum LimitType {

    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP
}
