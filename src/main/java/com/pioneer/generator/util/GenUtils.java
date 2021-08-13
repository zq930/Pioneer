package com.pioneer.generator.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.pioneer.common.constant.GenConstants;
import com.pioneer.generator.config.GenConfig;
import com.pioneer.generator.domain.GenTable;
import com.pioneer.generator.domain.GenTableColumn;

/**
 * 代码生成工具类
 *
 * @author hlm
 * @date 2021-08-12 12:59:55
 */
public class GenUtils {

    /**
     * 初始化表信息
     */
    public static void initTable(GenTable genTable, String operName) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName(GenConfig.packageName);
        genTable.setModuleName(getModuleName(GenConfig.packageName));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(StrUtil.replace(genTable.getTableComment(), "表", StrUtil.EMPTY));
        genTable.setFunctionAuthor(GenConfig.author);
        genTable.setCreateBy(operName);
    }

    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenTableColumn column, GenTable table) {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        column.setCreateBy(table.getCreateBy());
        // 设置java字段名
        column.setJavaField(StrUtil.toCamelCase(columnName));
        // 设置默认类型
        column.setJavaType(GenConstants.TYPE_STRING);

        if (ArrayUtil.contains(GenConstants.COLUMNTYPE_STR, dataType) || ArrayUtil.contains(GenConstants.COLUMNTYPE_TEXT, dataType)) {
            // 字符串长度超过500设置为文本域
            Integer columnLength = getColumnLength(column.getColumnType());
            String htmlType = columnLength >= 500 || ArrayUtil.contains(GenConstants.COLUMNTYPE_TEXT, dataType) ? GenConstants.HTML_TEXTAREA : GenConstants.HTML_INPUT;
            column.setHtmlType(htmlType);
        } else if (ArrayUtil.contains(GenConstants.COLUMNTYPE_TIME, dataType)) {
            column.setJavaType(GenConstants.TYPE_DATE);
            column.setHtmlType(GenConstants.HTML_DATETIME);
        } else if (ArrayUtil.contains(GenConstants.COLUMNTYPE_NUMBER, dataType)) {
            column.setHtmlType(GenConstants.HTML_INPUT);

            String[] str = StrUtil.splitToArray(StrUtil.subBetween(column.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
                // 如果是浮点型 统一用BigDecimal
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            } else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
                // 如果是整形
                column.setJavaType(GenConstants.TYPE_INTEGER);
            } else {
                // 长整形
                column.setJavaType(GenConstants.TYPE_LONG);
            }
        }

        // 插入字段（默认所有字段都需要插入）
        column.setIsInsert(GenConstants.REQUIRE);

        // 编辑字段
        if (!ArrayUtil.contains(GenConstants.COLUMNNAME_NOT_EDIT, columnName) && !column.isPk()) {
            column.setIsEdit(GenConstants.REQUIRE);
        }
        // 列表字段
        if (!ArrayUtil.contains(GenConstants.COLUMNNAME_NOT_LIST, columnName) && !column.isPk()) {
            column.setIsList(GenConstants.REQUIRE);
        }
        // 查询字段
        if (!ArrayUtil.contains(GenConstants.COLUMNNAME_NOT_QUERY, columnName) && !column.isPk()) {
            column.setIsQuery(GenConstants.REQUIRE);
        }

        // 查询字段类型
        if (StrUtil.endWithIgnoreCase(columnName, "name")) {
            column.setQueryType(GenConstants.QUERY_LIKE);
        }
        // 状态字段设置单选框
        if (StrUtil.endWithIgnoreCase(columnName, "status")) {
            column.setHtmlType(GenConstants.HTML_RADIO);
        }
        // 类型&性别字段设置下拉框
        else if (StrUtil.endWithIgnoreCase(columnName, "type")
                || StrUtil.endWithIgnoreCase(columnName, "sex")) {
            column.setHtmlType(GenConstants.HTML_SELECT);
        }
        // 图片字段设置图片上传控件
        else if (StrUtil.endWithIgnoreCase(columnName, "image")) {
            column.setHtmlType(GenConstants.HTML_IMAGE_UPLOAD);
        }
        // 文件字段设置文件上传控件
        else if (StrUtil.endWithIgnoreCase(columnName, "file")) {
            column.setHtmlType(GenConstants.HTML_FILE_UPLOAD);
        }
        // 内容字段设置富文本控件
        else if (StrUtil.endWithIgnoreCase(columnName, "content")) {
            column.setHtmlType(GenConstants.HTML_EDITOR);
        }
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        int lastIndex = packageName.lastIndexOf(StrUtil.DOT);
        int nameLength = packageName.length();
        return StrUtil.sub(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName) {
        int lastIndex = tableName.lastIndexOf(StrUtil.UNDERLINE);
        int nameLength = tableName.length();
        return StrUtil.sub(tableName, lastIndex + 1, nameLength);
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName) {
        boolean autoRemovePre = GenConfig.autoRemovePre;
        String tablePrefix = GenConfig.tablePrefix;
        if (autoRemovePre && StrUtil.isNotEmpty(tablePrefix)) {
            String[] searchList = tablePrefix.split(StrUtil.COMMA);
            tableName = replaceFirst(tableName, searchList);
        }
        return StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
    }

    /**
     * 批量替换前缀
     *
     * @param replacement 替换值
     * @param searchList  替换列表
     * @return 结果
     */
    public static String replaceFirst(String replacement, String[] searchList) {
        String text = replacement;
        for (String searchString : searchList) {
            if (replacement.startsWith(searchString)) {
                text = replacement.replaceFirst(searchString, StrUtil.EMPTY);
                break;
            }
        }
        return text;
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (StrUtil.indexOf(columnType, '(') > 0) {
            return StrUtil.subBefore(columnType, "(", false);
        } else {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType) {
        if (StrUtil.indexOf(columnType, '(') > 0) {
            String length = StrUtil.subBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        } else {
            return 0;
        }
    }
}
