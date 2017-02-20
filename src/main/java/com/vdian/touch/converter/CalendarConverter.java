package com.vdian.touch.converter;

import com.google.common.base.Strings;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author jifang
 * @since 16/10/21 上午8:30.
 */
public class CalendarConverter implements Converter<Calendar> {

    @Override
    public boolean can(Class<?> argType, Type[] actualTypes) {
        return argType == Calendar.class;
    }

    @Override
    public Calendar convert(Class<?> argType, Type[] actualTypes, String argString) throws Exception {
        Calendar calendar = Calendar.getInstance();
        if (!Strings.isNullOrEmpty(argString)) {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(argString));
        }
        return calendar;
    }
}
