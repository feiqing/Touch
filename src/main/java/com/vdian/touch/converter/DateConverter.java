package com.vdian.touch.converter;

import com.vdian.touch.utils.TouchUtils;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author jifang
 * @since 16/10/24 下午7:42.
 */
public class DateConverter implements Converter<Date> {

    @Override
    public boolean can(Class<?> argType, Type[] actualTypes) {
        return argType == Date.class;
    }

    @Override
    public Date convert(Class<?> argType, Type[] actualTypes, String argString) throws Exception {
        return TouchUtils.dateFormat(argString);
    }
}
