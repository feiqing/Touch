package com.vdian.touch.converter;

import java.lang.reflect.Type;

/**
 * @author jifang
 * @since 16/10/20 下午11:42.
 */
public interface Converter<T> {

    boolean can(Class<?> argType, Type[] actualTypes);

    T convert(Class<?> argType, Type[] actualTypes, String argString) throws Exception;
}
