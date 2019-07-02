package com.wtz.tools.design_patterns.singleton;

/**
 * 不直接使用公有静态final成员，使用简单工厂方法
 */
public class B {
    private static final B INSTANCE = new B();// 及早实例化，可以保证线程安全

    private B() {}

    public static B getInstance() {// 公有域方法好处在于清楚地声明了这是一个单例
        return INSTANCE;
    }
}
