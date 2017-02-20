package com.vdian.touch.helper;

import javax.servlet.ServletContext;

/**
 * @author jifang
 * @since 16/10/24 下午3:19.
 */
public abstract class AbstractHelper {

    private ServletContext servletContext;

    protected AbstractHelper(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected ServletContext getServletContext() {
        return this.servletContext;
    }
}
