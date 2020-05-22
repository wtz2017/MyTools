package com.wtz.tools.design_patterns.singleton;

public class D {

    private D() {}

    /**
     * 静态内部类的加载不需要依附外部类，在首次使用时才加载
     */
    private static class SingletonHolder {
        private static final D INSTANCE = new D();
    }

    /**
     * 在第一次加载 SingletonHolder 时初始化一次 INSTANCE 对象，保证唯一性，也延迟了单例的实例化
     */
    public static D getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
