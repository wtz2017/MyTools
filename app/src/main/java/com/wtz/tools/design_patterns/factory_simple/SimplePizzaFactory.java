package com.wtz.tools.design_patterns.factory_simple;

public class SimplePizzaFactory {
    public static Pizza createPizza(String type) {
        Pizza pizza = null;

        if ("PizzaA".equals(type)) {
            pizza = new PizzaA();
        } else if ("PizzaB".equals(type)) {
            pizza = new PizzaB();
        }

        return pizza;
    }
}
