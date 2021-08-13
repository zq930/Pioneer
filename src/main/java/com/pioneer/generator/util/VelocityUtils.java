package com.pioneer.generator.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.pioneer.common.constant.GenConstants;
import com.pioneer.generator.constant.VmConstant;
import com.pioneer.generator.domain.GenTable;
import com.pioneer.generator.domain.GenTableColumn;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 模板处理工具类
 *
 * @author hlm
 * @date 2021-08-12 12:45:53
 */
public class VelocityUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "main/java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /**
     * 默认上级菜单，系统工具
     */
    private static final String DEFAULT_PARENT_MENU_ID = "3";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable) {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String tplCategory = genTable.getTplCategory();
        String functionName = genTable.getFunctionName();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", genTable.getTplCategory());
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StrUtil.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StrUtil.lowerFirst(genTable.getClassName()));
        velocityContext.put("moduleName", genTable.getModuleName());
        velocityContext.put("BusinessName", StrUtil.upperFirst(genTable.getBusinessName()));
        velocityContext.put("businessName", genTable.getBusinessName());
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", DateUtil.now());
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);
        setMenuVelocityContext(velocityContext, genTable);
        if (GenConstants.TPL_TREE.equals(tplCategory)) {
            setTreeVelocityContext(velocityContext, genTable);
        }
        if (GenConstants.TPL_SUB.equals(tplCategory)) {
            setSubVelocityContext(velocityContext, genTable);
        }
        return velocityContext;
    }

    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable) {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        String parentMenuId = getParentMenuId(paramsObj);
        context.put("parentMenuId", parentMenuId);
    }

    public static void setTreeVelocityContext(VelocityContext context, GenTable genTable) {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        String treeCode = getTreeCode(paramsObj);
        String treeParentCode = getTreeParentCode(paramsObj);
        String treeName = getTreeName(paramsObj);

        context.put("treeCode", treeCode);
        context.put("treeParentCode", treeParentCode);
        context.put("treeName", treeName);
        context.put("expandColumn", getExpandColumn(genTable));
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) {
            context.put("tree_parent_code", paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        }
        if (paramsObj.containsKey(GenConstants.TREE_NAME)) {
            context.put("tree_name", paramsObj.getString(GenConstants.TREE_NAME));
        }
    }

    public static void setSubVelocityContext(VelocityContext context, GenTable genTable) {
        GenTable subTable = genTable.getSubTable();
        String subTableName = genTable.getSubTableName();
        String subTableFkName = genTable.getSubTableFkName();
        String subClassName = genTable.getSubTable().getClassName();
        String subTableFkClassName = StrUtil.upperFirst(StrUtil.toCamelCase(subTableFkName));

        context.put("subTable", subTable);
        context.put("subTableName", subTableName);
        context.put("subTableFkName", subTableFkName);
        context.put("subTableFkClassName", subTableFkClassName);
        context.put("subTableFkclassName", StrUtil.lowerFirst(subTableFkClassName));
        context.put("subClassName", subClassName);
        context.put("subclassName", StrUtil.lowerFirst(subClassName));
        context.put("subImportList", getImportList(genTable.getSubTable()));
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList(String tplCategory) {
        List<String> templates = new ArrayList<>(Arrays.asList(VmConstant.MENU_JAVA + VmConstant.DOMAIN,
                VmConstant.MENU_JAVA + VmConstant.MAPPER,
                VmConstant.MENU_JAVA + VmConstant.SERVICE,
                VmConstant.MENU_JAVA + VmConstant.SERVICE_IMPL,
                VmConstant.MENU_JAVA + VmConstant.CONTROLLER,
                VmConstant.MENU_XML + VmConstant.XML,
                VmConstant.MENU_SQL + VmConstant.SQL,
                VmConstant.MENU_JS + VmConstant.JS));
        if (GenConstants.TPL_CRUD.equals(tplCategory)) {
            templates.add(VmConstant.MENU_VUE + VmConstant.VUE);
        } else if (GenConstants.TPL_TREE.equals(tplCategory)) {
            templates.add(VmConstant.MENU_VUE + VmConstant.TREE);
        } else if (GenConstants.TPL_SUB.equals(tplCategory)) {
            templates.add(VmConstant.MENU_VUE + VmConstant.VUE);
            templates.add(VmConstant.MENU_JAVA + VmConstant.SUB_DOMAIN);
        }
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable) {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String javaPath = PROJECT_PATH + "/" + StrUtil.replace(packageName, ".", "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue";

        if (template.contains(VmConstant.DOMAIN)) {
            fileName = StrUtil.format("{}/domain/{}.java", javaPath, className);
        }
        if (template.contains(VmConstant.SUB_DOMAIN) && StrUtil.equals(GenConstants.TPL_SUB, genTable.getTplCategory())) {
            fileName = StrUtil.format("{}/domain/{}.java", javaPath, genTable.getSubTable().getClassName());
        } else if (template.contains(VmConstant.MAPPER)) {
            fileName = StrUtil.format("{}/mapper/{}Mapper.java", javaPath, className);
        } else if (template.contains(VmConstant.SERVICE)) {
            fileName = StrUtil.format("{}/service/I{}Service.java", javaPath, className);
        } else if (template.contains(VmConstant.SERVICE_IMPL)) {
            fileName = StrUtil.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        } else if (template.contains(VmConstant.CONTROLLER)) {
            fileName = StrUtil.format("{}/controller/{}Controller.java", javaPath, className);
        } else if (template.contains(VmConstant.XML)) {
            fileName = StrUtil.format("{}/{}Mapper.xml", mybatisPath, className);
        } else if (template.contains(VmConstant.SQL)) {
            fileName = businessName + "Menu.sql";
        } else if (template.contains(VmConstant.JS)) {
            fileName = StrUtil.format("{}/api/{}/{}.js", vuePath, moduleName, businessName);
        } else if (template.contains(VmConstant.VUE)) {
            fileName = StrUtil.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        } else if (template.contains(VmConstant.TREE)) {
            fileName = StrUtil.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return StrUtil.sub(packageName, 0, lastIndex);
    }

    /**
     * 根据列类型获取导入包
     *
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTable genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        GenTable subGenTable = genTable.getSubTable();
        HashSet<String> importList = new HashSet<>();
        if (ObjectUtil.isNotNull(subGenTable)) {
            importList.add("java.util.List");
        }
        for (GenTableColumn column : columns) {
            if (!column.isSuperColumn() && GenConstants.TYPE_DATE.equals(column.getJavaType())) {
                importList.add("java.time.LocalDateTime");
            } else if (!column.isSuperColumn() && GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType())) {
                importList.add("java.math.BigDecimal");
            }
        }
        return importList;
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName   模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    public static String getPermissionPrefix(String moduleName, String businessName) {
        return StrUtil.format("{}:{}", moduleName, businessName);
    }

    /**
     * 获取上级菜单ID字段
     *
     * @param paramsObj 生成其他选项
     * @return 上级菜单ID字段
     */
    public static String getParentMenuId(JSONObject paramsObj) {
        if (ObjectUtil.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.PARENT_MENU_ID)
                && StrUtil.isNotEmpty(paramsObj.getString(GenConstants.PARENT_MENU_ID))) {
            return paramsObj.getString(GenConstants.PARENT_MENU_ID);
        }
        return DEFAULT_PARENT_MENU_ID;
    }

    /**
     * 获取树编码
     *
     * @param paramsObj 生成其他选项
     * @return 树编码
     */
    public static String getTreeCode(JSONObject paramsObj) {
        if (paramsObj.containsKey(GenConstants.TREE_CODE)) {
            return StrUtil.toCamelCase(paramsObj.getString(GenConstants.TREE_CODE));
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取树父编码
     *
     * @param paramsObj 生成其他选项
     * @return 树父编码
     */
    public static String getTreeParentCode(JSONObject paramsObj) {
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) {
            return StrUtil.toCamelCase(paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取树名称
     *
     * @param paramsObj 生成其他选项
     * @return 树名称
     */
    public static String getTreeName(JSONObject paramsObj) {
        if (paramsObj.containsKey(GenConstants.TREE_NAME)) {
            return StrUtil.toCamelCase(paramsObj.getString(GenConstants.TREE_NAME));
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取需要在哪一列上面显示展开按钮
     *
     * @param genTable 业务表对象
     * @return 展开按钮列序号
     */
    public static int getExpandColumn(GenTable genTable) {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        String treeName = paramsObj.getString(GenConstants.TREE_NAME);
        int num = 0;
        for (GenTableColumn column : genTable.getColumns()) {
            if (column.isList()) {
                num++;
                String columnName = column.getColumnName();
                if (columnName.equals(treeName)) {
                    break;
                }
            }
        }
        return num;
    }
}
