package com.vdian.touch.converter;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jifang
 * @since 16/10/25 上午10:26.
 */
public class SetConverter implements Converter<Set> {

    @Override
    public boolean can(Class<?> argType, Type[] actualTypes) {
        return Set.class.isAssignableFrom(argType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set convert(Class<?> argType, Type[] actualTypes, String argString) throws Exception {
        Class<?> actualType = (actualTypes != null && actualTypes.length > 0) ? (Class<?>) actualTypes[0] : Object.class;

        List<?> list = JSONArray.parseArray(argString, actualType);
        Set set = new HashSet(list.size());
        for (Object object : list) {
            set.add(object);
        }
        
        return set;
    }
}
