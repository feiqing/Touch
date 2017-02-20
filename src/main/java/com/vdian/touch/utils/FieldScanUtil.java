package com.vdian.touch.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author jifang
 * @since 2016/12/11 下午03:24.
 */
public class FieldScanUtil {

    public static List<MethodEntity> scanField(Field field) {
        String name = field.getName();
        Class<?> type = field.getType();
        Type[] actualTypes = null;

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
        }

        MethodEntity entity = new MethodEntity(name, type, actualTypes);
        return Collections.singletonList(entity);
    }
}
