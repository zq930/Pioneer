package com.pioneer;

import io.github.yedaxia.apidocs.Docs;
import io.github.yedaxia.apidocs.DocsConfig;

import java.io.File;

/**
 * 生成api文档
 *
 * @author hlm
 * @date 2021-08-11 15:10:43
 */
public class BuildApiDoc {

    public static void main(String[] args) {
        DocsConfig config = new DocsConfig();
        // 项目路径
        config.setProjectPath(new File("").getAbsolutePath());
        // 版本
        config.setApiVersion("1.0");
        // 是否自动生成：true则扫描所有接口，false则只扫描有@ApiDoc注解的接口
        config.setAutoGenerate(true);
        Docs.buildHtmlDocs(config);
    }
}
