package com.vdian.touch.utils;

import java.lang.reflect.Type;

/**
 * @author jifang
 * @since 2016/11/17 上午10:50.
 */
public class MethodEntity {

    private String argName;

    private Class<?> argType;

    private Type[] actualTypes;

    public MethodEntity(String argName, Class<?> argType, Type[] actualTypes) {
        this.argName = argName;
        this.argType = argType;
        this.actualTypes = actualTypes;
    }

    public Type[] getActualTypes() {
        return actualTypes;
    }

    public void setActualTypes(Type[] actualTypes) {
        this.actualTypes = actualTypes;
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    public Class<?> getArgType() {
        return argType;
    }

    public void setArgType(Class<?> argType) {
        this.argType = argType;
    }
}
