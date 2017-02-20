package com.vdian.touch.helper;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vdian.touch.constant.Constant;
import com.vdian.touch.exceptions.TouchException;
import com.vdian.touch.utils.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author jifang
 * @since 16/10/24 下午2:13.
 */
public class TouchContextHelper extends AbstractHelper {

    private static final String CONTEXT_KEY = "touch_context_key";

    private static final String CONTEXT_SIZE = "touch_context_size";

    private static final Map<Class, Object> beanInstanceCache = new ConcurrentHashMap<>();

    public TouchContextHelper(ServletContext servletContext) {
        super(servletContext);
    }

    @SuppressWarnings("unchecked")
    public TouchContextHelper init() {
        LoadingCache<String, TouchEntity> touchContext =
                (LoadingCache<String, TouchEntity>) getServletContext().getAttribute(CONTEXT_KEY);

        if (isNeedInit(touchContext)) {
            try {
                touchContext = this.reInit();
            } catch (Exception e) {
                throw new TouchException(e);
            }
            getServletContext().setAttribute(CONTEXT_SIZE, touchContext.size());
            getServletContext().setAttribute(CONTEXT_KEY, touchContext);
        }

        return this;
    }

    private boolean isNeedInit(LoadingCache<String, TouchEntity> touchContext) {
        return touchContext == null
                || getServletContext().getAttribute(CONTEXT_SIZE) == null
                || (long) getServletContext().getAttribute(CONTEXT_SIZE) != touchContext.size();
    }

    @SuppressWarnings("unchecked")
    private LoadingCache<String, TouchEntity> reInit() throws Exception {
        LoadingCache<String, TouchEntity> touchContext = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build(new CacheLoader<String, TouchEntity>() {
                    @Override
                    public TouchEntity load(String key) throws Exception {
                        return null;
                    }
                });

        WebApplicationContext springContext = ContextLoader.getCurrentWebApplicationContext();
        // WebApplicationContext springContext =
        // WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        Object touchPackages = getServletContext().getAttribute(Constant.PACKAGES);
        for (String touchPackage : (Collection<String>) touchPackages) {

            // scan package
            Map<AccessibleObject, Class<?>> touchMethodMap = PackageScanUtil.scanPackage(touchPackage);

            // to TouchEntity
            for (Map.Entry<AccessibleObject, Class<?>> entry : touchMethodMap.entrySet()) {
                AccessibleObject accessible = entry.getKey();
                Class<?> clazz = entry.getValue();

                String touchPattern = TouchUtils.getTouchPattern(accessible);
                Object instance = getBeanInstance(springContext, clazz);

                List<MethodEntity> methodEntities;
                if (accessible instanceof Method) {
                    methodEntities = MethodScanUtil.scanMethod((Method) accessible);
                } else {
                    methodEntities = FieldScanUtil.scanField((Field) accessible);
                }

                touchContext.put(touchPattern, new TouchEntity(accessible, methodEntities, instance));
            }
        }

        if (touchContext.size() == 0) {
            throw new TouchException("could not found a correct touch entity, " +
                    "please check the touch.xml <packages><package .../><packages> correct");
        }

        return touchContext;
    }


    @SuppressWarnings("unchecked")
    public Map<String, String> listPatterns() {
        LoadingCache<String, TouchEntity> touchContext = this.getTouchContext();

        Set<String> patterns = touchContext.asMap().keySet();
        Map<String, String> patternMap = new HashMap<>(patterns.size());
        for (String pattern : patterns) {
            TouchEntity entity = touchContext.getUnchecked(pattern);
            String className = entity.getInstance().getClass().getName();
            String methodName = TouchUtils.getName(entity.getAccessibleObject());

            patternMap.put(pattern, className + " -> " + methodName);
        }

        return patternMap;
    }

    public TouchEntity getContextEntity(String touchPattern) {
        TouchEntity entity = this.getTouchContext().getIfPresent(touchPattern);
        if (entity == null) {
            throw new TouchException("please check touchPattern " + touchPattern + " corresponding");
        }

        return entity;
    }


    @SuppressWarnings("unchecked")
    private LoadingCache<String, TouchEntity> getTouchContext() {
        this.init();
        return (LoadingCache<String, TouchEntity>) getServletContext().getAttribute(CONTEXT_KEY);
    }

    private Object getBeanInstance(ApplicationContext context, Class<?> clazz) throws Exception {
        Object instance = beanInstanceCache.get(clazz);
        if (instance == null) {
            try {
                instance = context.getBean(clazz);
            } catch (NoSuchBeanDefinitionException e) {
                // for aop
                String beanName = getBeanName(context, clazz);
                if (!Strings.isNullOrEmpty(beanName)) {
                    Object proxyBean = context.getBean(beanName);
                    instance = AOPTargetUtils.getTarget(proxyBean);
                } else {
                    throw e;
                }
            }

            beanInstanceCache.put(clazz, instance);
        }

        return instance;
    }

    // TODO make sure bean have none alias
    private String getBeanName(ApplicationContext context, Class<?> clazz) {
        String className = clazz.getSimpleName();
        String[] definitionNames = context.getBeanDefinitionNames();
        for (String definitionName : definitionNames) {
            if (className.equalsIgnoreCase(definitionName)) {
                return definitionName;
            }
        }
        return null;
    }
}
