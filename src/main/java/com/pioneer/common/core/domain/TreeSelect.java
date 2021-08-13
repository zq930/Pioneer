package com.pioneer.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pioneer.web.system.domain.SysDept;
import com.pioneer.web.system.domain.SysMenu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TreeSelect
 *
 * @author hlm
 * @date 2021-08-09 17:43:48
 */
@Data
public class TreeSelect implements Serializable {

    private static final long serialVersionUID = 2191078146544051485L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect(SysDept dept) {
        this.id = dept.getDeptId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenu menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }
}
