package com.wtz.tools;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class JVMJarTools {

    protected static Method addURL = null;
    static {
        try {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            addURL.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 适用于JVM动态加载Jar包到内存中并创建指定类实例
     */
    public static Object loadClassJar(String jarFilePath, String className, Class[] argTypes,
            Object[] argValues) throws Exception {
        File file = new File(jarFilePath);
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + jarFilePath);
        }
        URL jarURL = file.toURI().toURL();

        // ClassLoader cl = new URLClassLoader(new URL[] {});
        // ClassLoader cl = new URLClassLoader(new URL[] {},
        // Thread.currentThread().getContextClassLoader());
        // ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        addURL.invoke(cl, new Object[] { jarURL });

        // Class clazz = Class.forName(className);
        // Class clazz = Class.forName(className, false, cl);
        Class clazz = cl.loadClass(className);

        // return clazz.newInstance();
        Constructor constructor = clazz.getDeclaredConstructor(argTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(argValues);
    }
}
