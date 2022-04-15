package com.pioneer.common.enums;

/**
 * 用户状态
 *
 * @author hlm
 * @date 2021-08-09 17:45:14
 */
public enum UserStatus {

    // 正常
    OK("0"),

    // 停用
    DISABLE("1"),

    // 删除
    DELETED("2");

    private final String code;

    UserStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
