package com.vdian.touch.cases;

/**
 * @author jifang
 * @since 16/1/27 下午4:01.
 */
public enum BlackType {

    FEED(1, "动态"),
    USER(2, "用户");

    private int type;

    private String description;

    BlackType(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getValue() {
        return type;
    }

    public String getDescription() {
        return description;
    }

}
