package com.chen.LeoBlog.enums;

public enum RoleEnum {
    VISITOR(1, "游客"),
    USER(2, "普通用户"),
    ADMIN(3, "管理员"),
    ;

    private final Integer type;
    private final String desc;

    RoleEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static RoleEnum of(Integer type) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
