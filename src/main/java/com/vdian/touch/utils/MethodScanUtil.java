package com.vdian.touch.utils;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author jifang
 * @since 2016/11/17 上午10:54.
 */
public class MethodScanUtil {

    private static final ClassPool pool;

    static {
        pool = ClassPool.getDefault();
        // 考虑自定义自定义ClassLoader的情况
        pool.insertClassPath(new ClassClassPath(MethodScanUtil.class));
    }

    public static List<MethodEntity> scanMethod(Method method) throws NotFoundException {
        // ct method
        CtMethod ctMethod = getCtMethod(method);

        // method local variable
        Object[] variableTable = getMethodVariableMap(ctMethod).values().toArray();

        // variable start position
        int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;

        // parameter types
        Class<?>[] argTypes = method.getParameterTypes();
        Type[] argGenericTypes = method.getGenericParameterTypes();

        List<MethodEntity> methodEntities = new ArrayList<>(argTypes.length);
        for (int i = 0; i < argTypes.length; ++i) {
            String argName = (String) variableTable[i + pos];
            Class<?> argType = argTypes[i];
            Type argGenericType = argGenericTypes[i];

            if (argGenericType instanceof ParameterizedType) {
                Type[] actualTypes = ((ParameterizedType) argGenericType).getActualTypeArguments();
                methodEntities.add(new MethodEntity(argName, argType, actualTypes));
            } else {
                methodEntities.add(new MethodEntity(argName, argType, null));
            }
        }

        return methodEntities;
    }


    private static SortedMap<Integer, String> getMethodVariableMap(CtMethod ctMethod) {

        // 拿到Class文件内局部变量表
        MethodInfo methodInfo = ctMethod.getMethodInfo2();
        LocalVariableAttribute attribute = (LocalVariableAttribute) methodInfo.getCodeAttribute()
                .getAttribute(LocalVariableAttribute.tag);

        // 组织为有序Map
        SortedMap<Integer, String> variableMap = new TreeMap<>();
        for (int i = 0; i < attribute.tableLength(); i++) {
            variableMap.put(attribute.index(i), attribute.variableName(i));
        }

        return variableMap;
    }

    private static CtMethod getCtMethod(Method method) throws NotFoundException {

        // method ct class
        CtClass ctClazz = getCtClass(method.getDeclaringClass());

        // method param ct classes
        Class<?>[] pTypes = method.getParameterTypes();
        CtClass[] pCtClasses = new CtClass[pTypes.length];
        for (int i = 0; i < pTypes.length; ++i) {
            pCtClasses[i] = getCtClass(pTypes[i]);
        }

        return ctClazz.getDeclaredMethod(method.getName(), pCtClasses);
    }

    private static CtClass getCtClass(Class<?> clazz) throws NotFoundException {
        return pool.getCtClass(clazz.getName());
    }
}
