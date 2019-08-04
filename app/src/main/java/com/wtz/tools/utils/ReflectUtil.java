package com.wtz.tools.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ReflectUtil {
    private static final String TAG = "ReflectUtil";

    /**
     * 对于类似如下获取泛型参数类型的Class的条件是：
     * 泛型参数类型必须是明确指定的具体类型，不可以是 TypeVariable
     * (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[index]
     * (Class<T>) ((ParameterizedType) getClass().getGenericInterfaces()[index]).getActualTypeArguments()[index]
     *
     * 测试泛型结果：
     * A-->getGenericInterfaces: interface com.wtz.tools.utils.ReflectUtil$IA1 is Class
     * A-->getGenericInterfaces: com.wtz.tools.utils.ReflectUtil$IA2<java.lang.Boolean> is ParameterizedType
     *   ParameterizedType info is:
     *      getRawType: interface com.wtz.tools.utils.ReflectUtil$IA2
     *      getOwnerType: class com.wtz.tools.utils.ReflectUtil
     *      getActualTypeArguments: class java.lang.Boolean(Class);
     * A-->getGenericInterfaces: com.wtz.tools.utils.ReflectUtil$IA3<java.lang.String, T> is ParameterizedType
     *   ParameterizedType info is:
     *      getRawType: interface com.wtz.tools.utils.ReflectUtil$IA3
     *      getOwnerType: class com.wtz.tools.utils.ReflectUtil
     *      getActualTypeArguments: class java.lang.String(Class);T(TypeVariable);
     * B getGenericSuperclass:com.wtz.tools.utils.ReflectUtil$A<java.lang.Integer>
     * B tClass:class java.lang.Integer
     */
    void testGeneric() {
        new A<Long>().print();
        new B().print();
    }

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

    public static Object[] getAllFieldValues(Class clazz, Object instance) {
        Object[] objects = null;
        try {
            Field[] fields = clazz.getDeclaredFields();
            objects = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                objects[i] = getFieldValue(clazz, instance, fields[i].getName());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return objects;
    }

    public static Object getFieldValue(Class clazz, Object instance, String fieldName) {
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

    /**
     * 从目标实例 instance 中获取成员变量 field 的值
     *
     * @param field
     * @param instance
     * @param <T>
     * @return
     */
    public static <T> T getFieldValue(Object instance, Field field) {
        if (field == null) {
            return null;
        }

        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getStaticFieldValue(Field field) {
        if (field == null) {
            return null;
        }

        try {
            return (T) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class[] argTypes,
                                            Object[] argValues, boolean hasReturnValue) {
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
     *
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

    interface IA1 {
    }

    interface IA2<T> {
    }

    interface IA3<K, V> {
    }

    class A<T> implements IA1, IA2<Boolean>, IA3<String, T> {
        void print() {
            Type[] types = getClass().getGenericInterfaces();
            StringBuilder builder = new StringBuilder();
            for (Type type : types) {
                Log.d(TAG, "A-->getGenericInterfaces: " + type + " is " + getTypeName(type));
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    builder.setLength(0);
                    builder.append(" ParameterizedType info is:\n");
                    builder.append(" getRawType: " + pt.getRawType() + "\n");
                    builder.append(" getOwnerType: " + pt.getOwnerType() + "\n");
                    builder.append(" getActualTypeArguments: ");
                    Type[] atas = pt.getActualTypeArguments();
                    for (Type aType : atas) {
                        builder.append(aType);
                        builder.append("(");
                        builder.append(getTypeName(aType));
                        builder.append(")");
                        builder.append(";");
                    }
                    Log.d(TAG, builder.toString());
                }
            }
        }
    }

    class B extends A<Integer> {
        void print() {
            Type type = getClass().getGenericSuperclass();
            Log.d(TAG, "B getGenericSuperclass:" + type);
            if (type instanceof ParameterizedType) {
                Class<?> tClass = (Class<?>) ((ParameterizedType) type)
                        .getActualTypeArguments()[0];
                Log.d(TAG, "B tClass:" + tClass);
            }
        }
    }

    public static String getTypeName(Type type) {
        String name = "unknown";
        if (type instanceof Class) {
            name = "Class";
        } else if (type instanceof ParameterizedType) {
            name = "ParameterizedType";
        } else if (type instanceof GenericArrayType) {
            name = "GenericArrayType";
        } else if (type instanceof TypeVariable) {
            name = "TypeVariable";
        }
        return name;
    }

}
