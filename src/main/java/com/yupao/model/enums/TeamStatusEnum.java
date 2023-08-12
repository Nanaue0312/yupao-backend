package com.yupao.model.enums;

/**
 * 队伍状态枚举
 */
public enum TeamStatusEnum {
    /**
     * 公共类型
     */
    PUBLIC("公共", 0),
    /**
     * 私密类型
     */
    PRIVATE("私密", 1),
    /**
     * 加密类型
     */
    SECRET("加密", 2);

    TeamStatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }


    private final String name;
    private final Integer status;

    public String getName() {
        return name;
    }

    public Integer getStatus() {
        return status;
    }

    public static TeamStatusEnum getTeamStatusEnum(Integer status) {
        if (status == null) {
            return null;
        }
        for (TeamStatusEnum value : TeamStatusEnum.values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
