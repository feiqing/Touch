package com.vdian.touch.utils;

import java.lang.reflect.AccessibleObject;
import java.util.List;

/**
 * @author jifang
 * @since 16/10/24 下午3:57.
 */
public class TouchEntity {

    private AccessibleObject accessibleObject;

    private List<MethodEntity> methodEntities;

    private Object instance;

    public TouchEntity(AccessibleObject accessibleObject, List<MethodEntity> methodEntities, Object instance) {
        this.accessibleObject = accessibleObject;
        this.methodEntities = methodEntities;
        this.instance = instance;
    }

    public AccessibleObject getAccessibleObject() {
        return accessibleObject;
    }

    public List<MethodEntity> getMethodEntities() {
        return methodEntities;
    }

    public Object getInstance() {
        return instance;
    }
}
