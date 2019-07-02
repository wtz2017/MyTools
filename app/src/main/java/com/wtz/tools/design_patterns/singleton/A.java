package com.wtz.tools.design_patterns.singleton;

/**
 * 如果应用总是创建并使用单例，或者在创建和运行时负担不太繁重，可以及早创建实例。
 * 使用公有静态final成员：
 */
public class A {
    public static final A INSTANCE = new A();// 及早实例化，可以保证线程安全

    private A() {}
}
