package com.vdian.touch.helper;

import com.vdian.touch.converter.Converter;
import com.vdian.touch.exceptions.TouchException;

import javax.servlet.ServletContext;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author jifang
 * @since 16/10/24 下午2:45.
 */
public class ConverterHelper extends AbstractHelper {

    private static final String CONVERTER_CONTEXT = "converter_context";

    public ConverterHelper(ServletContext servletContext) {
        super(servletContext);
    }

    public ConverterHelper init(Collection<String> converterNames) {
        if (getServletContext().getAttribute(CONVERTER_CONTEXT) == null) {
            Queue<Converter<?>> converters = new LinkedList<>();
            try {
                if (converterNames.isEmpty()) {
                    addDefault(converters);
                } else {
                    for (String converterName : converterNames) {
                        converters.add((Converter<?>) Class.forName(converterName).newInstance());
                    }
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new TouchException(e);
            }
            getServletContext().setAttribute(CONVERTER_CONTEXT, converters);
        }

        return this;
    }


    @SuppressWarnings("unchecked")
    public Converter<?> selectAvailableConverter(Class<?> aType, Type[] actualTypes) {

        Queue<Converter<?>> converters = (Queue<Converter<?>>) getServletContext().getAttribute(CONVERTER_CONTEXT);
        for (Converter<?> converter : converters) {
            if (converter.can(aType, actualTypes)) {
                return converter;
            }
        }

        return null;
    }

    private void addDefault(Queue<Converter<?>> converters) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        converters.add((Converter<?>) Class.forName("com.vdian.touch.converter.DateConverter").newInstance());
        converters.add((Converter<?>) Class.forName("com.vdian.touch.converter.SetConverter").newInstance());
        converters.add((Converter<?>) Class.forName("com.vdian.touch.converter.CalendarConverter").newInstance());
    }
}
