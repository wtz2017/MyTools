package com.wtz.tools.utils.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator implements Iterator {
    Object[] array;
    int elementCount;
    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such

    public ArrayIterator(Object[] array) {
        this.array = array;
        elementCount = array.length;
    }

    @Override
    public boolean hasNext() {
        return cursor < elementCount;
    }

    @Override
    public Object next() {
        if (cursor >= elementCount)
            throw new NoSuchElementException();

        lastRet = cursor;
        cursor++;
        return array[lastRet];
    }

    @Override
    public void remove() {
        if (lastRet < 0) {
            throw new IllegalStateException("" +
                    "You can't remove an item until you've done at least one next()");
        }

        //for (int i = position - 1; i < array.length - 1; i++) {
        //    array[i] = array[i + 1];
        //}
        // 参考 ArrayList 实现，用下述代码代替元素移动
        int numMoved = array.length - 1 - lastRet;
        if (numMoved > 0) {
            System.arraycopy(array, lastRet + 1, array, lastRet, numMoved);
        }

        array[--elementCount] = null;// 清空原来最后一个元素

        cursor = lastRet;// 把当前指针指向删除的位置
        lastRet = -1;
    }
}
