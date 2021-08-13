package com.pioneer.framework.web.domain.server;

import lombok.Getter;
import lombok.Setter;

/**
 * Cpu
 *
 * @author hlm
 * @date 2021-08-09 17:49:26
 */
@Getter
@Setter
public class Cpu {

    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * CPU总的使用率
     */
    private double total;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double used;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;
}
