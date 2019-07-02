package com.wtz.tools.design_patterns.singleton;

/**
 * 为了避免多线程并发，同时又考虑性能，采用“双重检查加锁”，保证只有第一次创建时才会同步；
 * 同时因为Java 平台内存模型允许所谓的“无序写入”，同步块外面的 if (instance == null)
 * 可能看到已存在但不完整的实例，JDK5.0及以后版本中使用 volatile 修饰可以解决这个问题。
 */
public class C {
    private static volatile C INSTANCE;

    private C() {}

    public static C getInstance() {
        if (INSTANCE == null) {
            synchronized (C.class) {
                if (INSTANCE == null) {// 延迟实例化
                    INSTANCE = new C();
                }
            }
        }
        return INSTANCE;
    }
}
