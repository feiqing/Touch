package com.vdian.touch.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.vdian.touch.converter.Converter;
import com.vdian.touch.helper.ConverterHelper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jifang
 * @since 16/10/24 下午3:30.
 */
public class ArgComposeUtil {

    public static Object[] composeArgs(String queryString, List<MethodEntity> methodEntities, ConverterHelper converterHelper) throws Exception {

        List<Object> args = new LinkedList<>();
        if (!Strings.isNullOrEmpty(queryString)) {
            JSONObject masterJson = JSONObject.parseObject(queryString);

            for (MethodEntity methodEntity : methodEntities) {
                String argName = methodEntity.getArgName();
                Class<?> argType = methodEntity.getArgType();
                Type[] actualTypes = methodEntity.getActualTypes();

                Object argInstance;
                String argValue = masterJson.getString(argName);
                Converter<?> converter = converterHelper.selectAvailableConverter(argType, actualTypes);
                if (converter != null) {
                    argInstance = converter.convert(argType, actualTypes, argValue);
                } else {
                    argInstance = newInstance(argValue, argType, actualTypes);
                    argInstance = (argInstance == null ? masterJson.getObject(argName, argType) : argInstance);
                }

                args.add(argInstance);
            }
        }

        return args.toArray();
    }

    public static Object composeField(String queryString, List<MethodEntity> entities, ConverterHelper converterHelper) throws Exception {
        MethodEntity fieldEntity = entities.get(0);
        Class<?> fieldType = fieldEntity.getArgType();
        Type[] actualTypes = fieldEntity.getActualTypes();

        Object fieldValue;
        Converter<?> converter = converterHelper.selectAvailableConverter(fieldType, actualTypes);
        if (converter != null) {
            fieldValue = converter.convert(fieldType, actualTypes, queryString);
        } else {
            fieldValue = newInstance(queryString, fieldType, actualTypes);
            fieldValue = (fieldValue == null ? JSONObject.parseObject(queryString, fieldType) : fieldValue);
        }

        return fieldValue;
    }

    private static Object newInstance(String argValue, Class<?> argType, Type[] actualTypes) {
        Object instance = null;

        if (argType == Character.class || argType == char.class) {
            instance = argValue.charAt(0);
        } else if (argType == String.class) {
            instance = argValue;
        } else if (argType == List.class || argType == Collection.class) {
            Class<?> actualType = (actualTypes != null && actualTypes.length > 0) ? (Class<?>) actualTypes[0] : Object.class;
            instance = JSONObject.parseArray(argValue, actualType);
        }

        return instance;
    }
}
