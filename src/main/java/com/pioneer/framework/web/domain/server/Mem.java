package com.pioneer.framework.web.domain.server;

import lombok.Getter;
import lombok.Setter;

/**
 * Mem
 *
 * @author hlm
 * @date 2021-08-09 17:49:56
 */
@Getter
@Setter
public class Mem {

    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    /**
     * 使用率
     */
    private double usage;
}
