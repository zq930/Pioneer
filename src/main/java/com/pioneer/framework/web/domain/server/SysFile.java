package com.pioneer.framework.web.domain.server;

import lombok.Getter;
import lombok.Setter;

/**
 * SysFile
 *
 * @author hlm
 * @date 2021-08-09 17:49:56
 */
@Getter
@Setter
public class SysFile {

    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 盘符类型
     */
    private String sysTypeName;

    /**
     * 文件类型
     */
    private String typeName;

    /**
     * 总大小
     */
    private String total;

    /**
     * 剩余大小
     */
    private String free;

    /**
     * 已经使用量
     */
    private String used;

    /**
     * 资源的使用率
     */
    private double usage;
}
