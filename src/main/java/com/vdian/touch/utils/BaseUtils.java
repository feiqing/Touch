package com.vdian.touch.utils;

import com.google.common.base.Strings;
import com.vdian.touch.Touch;
import com.vdian.touch.exceptions.TouchException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author jifang
 * @since 16/10/24 下午2:32.
 */
public class BaseUtils {

    private static final DateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static final Set<String> touchPatterns = new CopyOnWriteArraySet<>();

    public static String getTouchPattern(String requestURI) {
        int index = requestURI.lastIndexOf('/');
        if (index != -1) {
            requestURI = requestURI.substring(index + 1);
        }

        return requestURI;
    }

    public static String getTouchPattern(AccessibleObject accessibleObject) {
        Touch touch = accessibleObject.getAnnotation(Touch.class);
        String touchPattern = touch.touchPattern();
        if ("lists.do".equalsIgnoreCase(touchPattern)) {
            throw new TouchException("/touch/lists.do is by taking up for list all touch patterns");
        }

        String pattern;
        if (Strings.isNullOrEmpty(touchPattern)) {
            pattern = BaseUtils.getName(accessibleObject);
        } else {
            pattern = touchPattern;
        }

        if (touchPatterns.contains(pattern)) {
            throw new TouchException("duplicated touch pattern [" + pattern + "]");
        } else {
            touchPatterns.add(pattern);
        }

        return pattern;
    }

    public static String getQueryString(String queryString) throws UnsupportedEncodingException {
        if (!Strings.isNullOrEmpty(queryString)) {
            return URLDecoder.decode(queryString, "UTF-8");
        }
        return null;
    }

    public static Date dateFormat(String dateString) {
        synchronized (DEFAULT_DATE_FORMATTER) {
            try {
                return DEFAULT_DATE_FORMATTER.parse(dateString);
            } catch (ParseException e) {
                throw new TouchException(e);
            }
        }
    }

    public static String getName(AccessibleObject accessibleObject) {
        return accessibleObject instanceof Method ?
                ((Method) accessibleObject).getName() :
                ((Field) accessibleObject).getName();
    }

    public static Object getFieldValue(Field field, Object instance) throws IllegalAccessException {
        Object value = field.get(instance);
        return value == null ? "null" : value;
    }
}
