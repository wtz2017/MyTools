package com.wtz.tools.design_patterns.factory_method;

import com.wtz.tools.design_patterns.factory_simple.Pizza;

/**
 * 这里是披萨店主店
 * <p>
 * 在一个超类中，用到了某个产品对象，定义了一些操作该产品对象的方法，但产品对象的创建交给了一个抽象方法，
 * 由继承的子类具体实现创建产品对象的方法，这个抽象方法称为工厂方法，这个模式称为工厂方法模式。
 * <p>
 * 工厂方法模式创建了一个框架，把对象的创建工作推迟到子类实现，客户选择使用哪个子类，
 * 就决定了最终创建的产品是什么风格。
 * <p>
 * 使用方式是继承。
 */
public abstract class AbsPizzaStore {
    public Pizza orderPizza(String type) {

        Pizza pizza = createPizza(type);

        // 以下工艺流程不变
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();

        return pizza;
    }

    protected abstract Pizza createPizza(String type);// 工厂方法
}
