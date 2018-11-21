package com.wtz.tools.test.data;

public class FragmentItem {

    public FragmentItem(String name, String fragmentClassName) {
        this.name = name;
        this.fragmentClassName = fragmentClassName;
    }

    public String name;
    
    /**
     * e.g. "com.wtz.tools.test.FileUtilFragment"
     * i.e. FileUtilFragment.class.getName()
     */
    public String fragmentClassName;
}
