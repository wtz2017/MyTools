package com.wtz.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Class<?> getClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getClass(Object instance) {
        return (instance != null) ? instance.getClass() : null;
    }

    public static Object[] getAllFields(Class clazz, Object instance) {
        Object[] objects = null;
        try {
            Field[] fields = clazz.getDeclaredFields();
            objects = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                objects[i] = getField(clazz, instance, fields[i].getName());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return objects;
    }

    public static Object getField(Class clazz, Object instance, String fieldName) {
        Object result = null;
        try {
            // 非public要用declared获取
            Field field = clazz.getDeclaredField(fieldName);
            // 非public需要设置为可访问
            field.setAccessible(true);
            // 获取field所属对象下的值
            result = field.get(instance);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class[] argTypes,
            Object[] argValues, boolean hasReturnValue) {
        try {
            Constructor c1 = clazz.getDeclaredConstructor(String.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return invokeMethod(clazz, null, methodName, argTypes, argValues, hasReturnValue);
    }

    public static Object invokeMethod(Class<?> clazz, Object instance, String methodName,
            Class[] argTypes, Object[] argValues, boolean hasReturnValue) {
        Object result = null;

        Method m;
        try {
            // 非public要用declared获取
            m = clazz.getDeclaredMethod(methodName, argTypes);
            // 非public需要设置为可访问
            m.setAccessible(true);
            if (hasReturnValue) {
                result = m.invoke(instance, argValues);
            } else {
                m.invoke(instance, argValues);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }
    
    /**
     * 循环查找当前类及父类中目标方法
     * @param targetClass
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getDeclaredMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) {
        Method method = null;

        for (Class<?> clazz = targetClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        return null;
    }

    public static Object getClassInstance(Class<?> clazz, Class[] argTypes, Object[] argValues) {
        Object result = null;
        try {
            Constructor constructor = clazz.getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            result = constructor.newInstance(argValues);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}
