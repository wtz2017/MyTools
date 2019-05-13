package com.wtz.tools.test.data;

public class FragmentItem {

    public FragmentItem(String name, String fragmentClassName) {
        this.name = name;
        this.fragmentClassName = fragmentClassName;
    }

    public FragmentItem(String name, String fragmentClassName, boolean isActivity, Class clazz) {
        this.name = name;
        this.fragmentClassName = fragmentClassName;
        this.isActivity = isActivity;
        this.clazz = clazz;
    }

    public String name;
    
    /**
     * e.g. "com.wtz.tools.test.FileUtilFragment"
     * i.e. FileUtilFragment.class.getName()
     */
    public String fragmentClassName;

    public boolean isActivity;

    public Class clazz;

    @Override
    public String toString() {
        return "FragmentItem:{name:" + name + ";fragmentClassName:" + fragmentClassName + "}";
    }
}
