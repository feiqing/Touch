package com.vdian.touch.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * @author jifang
 * @since 2016/11/26 下午3:37.
 */
public class AOPTargetUtils {

    public static Object getTarget(Object bean) throws Exception {
        if (AopUtils.isAopProxy(bean)) {
            if (AopUtils.isJdkDynamicProxy(bean)) {
                bean = getJDKProxyTarget(bean);
            } else {
                bean = getCglibTarget(bean);
            }
        }

        return bean;
    }

    private static Object getJDKProxyTarget(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
    }

    private static Object getCglibTarget(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }
}
