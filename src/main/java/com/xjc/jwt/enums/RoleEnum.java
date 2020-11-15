package com.xjc.jwt.enums;

/**
 * @Version 1.0
 * @ClassName RoleEnum
 * @Author jiachenXu
 * @Date 2020/11/8
 * @Description
 */
public enum RoleEnum {

    USER("USER", "用户"),
    TEMP_USER("TEMP_USER", "临时用户"),
    MANAGER("MANAGER", "管理者"),
    ADMIN("ADMIN", "Admin");
    String name;
    String description;

    RoleEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
