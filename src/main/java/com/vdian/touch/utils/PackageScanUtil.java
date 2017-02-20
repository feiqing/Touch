package com.vdian.touch.utils;

import com.vdian.touch.Touch;
import com.vdian.touch.exceptions.TouchException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author jifang
 * @since 2016/10/27 上午9:38.
 */
public class PackageScanUtil {

    private static final String CLASS_SUFFIX = ".class";

    private static final String FILE_PROTOCOL = "file";

    private static final String JAR_PROTOCOL = "jar";

    public static Map<AccessibleObject, Class<?>> scanPackage(String packageName) throws IOException {
        Map<AccessibleObject, Class<?>> touchMethodMap = new HashMap<>();

        String packageDir = packageName.replace('.', '/');
        Enumeration<URL> packageResources = Thread.currentThread().getContextClassLoader().getResources(packageDir);
        while (packageResources.hasMoreElements()) {
            URL packageResource = packageResources.nextElement();

            String protocol = packageResource.getProtocol();
            // 项目内class
            if (FILE_PROTOCOL.equals(protocol)) {
                String packageDirPath = URLDecoder.decode(packageResource.getPath(), "UTF-8");
                scanProjectPackage(packageName, packageDirPath, touchMethodMap);
            }
            // jar包内class
            else if (JAR_PROTOCOL.equals(protocol)) {
                JarFile jar = ((JarURLConnection) packageResource.openConnection()).getJarFile();
                scanJarPackage(packageDir, jar, touchMethodMap);
            }
        }

        return touchMethodMap;
    }

    private static void scanJarPackage(String packageDir, JarFile jar, Map<AccessibleObject, Class<?>> touchMethodMap) throws IOException {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            String entryName = entries.nextElement().getName();
            if (entryName.startsWith(packageDir) && entryName.endsWith(CLASS_SUFFIX)) {
                String cassNameWithPackage = trimClassSuffix(entryName.replace('/', '.'));
                scanClass(cassNameWithPackage, touchMethodMap);
            }
        }
    }

    private static void scanProjectPackage(String packageName, String packageDirPath, Map<AccessibleObject, Class<?>> touchMethodMap) {

        File packageDirFile = new File(packageDirPath);
        if (packageDirFile.exists() && packageDirFile.isDirectory()) {

            File[] subFiles = packageDirFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(CLASS_SUFFIX);
                }
            });

            for (File subFile : subFiles) {
                if (subFile.isDirectory()) {
                    String subPackageName = packageName + "." + subFile.getName();
                    String subPackageDirPath = subFile.getAbsolutePath();

                    scanProjectPackage(subPackageName, subPackageDirPath, touchMethodMap);
                } else {
                    String className = trimClassSuffix(subFile.getName());

                    scanClass(packageName, className, touchMethodMap);
                }
            }
        }
    }

    // with .class suffix
    private static String trimClassSuffix(String classNameWithSuffix) {
        int endIndex = classNameWithSuffix.length() - CLASS_SUFFIX.length();
        return classNameWithSuffix.substring(0, endIndex);
    }

    private static void scanClass(String classNameWithPackage, Map<AccessibleObject, Class<?>> touchMethodMap) {
        try {
            Class<?> clazz = Class.forName(classNameWithPackage);
            Method[] methods = clazz.getDeclaredMethods();

            // scan method
            for (Method method : methods) {
                if (method.isAnnotationPresent(Touch.class)) {
                    method.setAccessible(true);

                    touchMethodMap.put(method, clazz);
                }
            }

            // scan fields: since 0.5-SNAPSHOT
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Touch.class)) {
                    field.setAccessible(true);

                    touchMethodMap.put(field, clazz);
                }
            }

        } catch (ClassNotFoundException ignored) {
            // 不可能存在
        }
    }

    private static void scanClass(String packageName, String className, Map<AccessibleObject, Class<?>> touchMethodMap) {
        String classNameWithPackage = packageName + "." + className;
        scanClass(classNameWithPackage, touchMethodMap);
    }

    public static void main(String[] args) throws IOException {
        //scanPackage("com.google.common.base");
        scanPackage("com.vdian.touch");
    }
}