package com.wtz.tools.utils;

import java.lang.reflect.Array;

public class SortUtils {

    /**
     * 插入排序：
     * 插入排序的基本思想是，每次将 1 个待排序的记录按其关键字大小插入到前面已经排好序的子序列中，
     * 寻找最适当的位置，直至全部记录插入完毕。
     * <p>
     * 给一数组[0, N],取i从1到N遍历，表示排序趟数；
     * 每一趟比较时，保证[0,i-1]已经有序，取第i位元素依次与[0,i-1]里的元素比较，找到正确位置插入
     *
     * @param array 数组引用变量
     * @param <T>   数组元素类型
     */
    public static <T extends Comparable<? super T>> void insertSort(T[] array) {
        T tmp;
        for (int i = 1; i < array.length; i++) {
            tmp = array[i];//保存当前位置p的元素，其中[0,p-1]已经有序
            int j;
            for (j = i; j > 0 && tmp.compareTo(array[j - 1]) < 0; j--) {
                array[j] = array[j - 1];//后移一位
            }
            array[j] = tmp;//插入到合适的位置
        }
    }


    /**
     * 冒泡排序：
     * 以从小到大排序为例：
     * 给一数组 [0, N]，第一层大循环取 i 从 0 到 N-1 遍历，第二层小循环取 j 从 0 到 N-1-i 遍历：
     * 在小循环中，逐一比较相邻的 j 和 j+1 两个元素，两者顺序不对就直接交换位置；
     * 这样一趟下来，第 N-i 位置就是最大的。
     *
     * @param array 数组引用变量
     * @param <T>   数组元素类型
     */
    public static <T extends Comparable<? super T>> void bubbleSort(T[] array) {
        T temp;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j].compareTo(array[j + 1]) > 0) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 选择排序：
     * 选择排序算法的基本思路是为每一个位置选择当前最小的元素。
     * <p>
     * 与冒泡排序类似，也是两层循环，第一层循环表示比较的趟数，第二层循环表示每一趟具体的比较；
     * 区别在于第二层循环比较中，冒泡排序是逐一比较相邻的两个元素，两者顺序不对就直接交换位置；
     * 而选择排序是从前往后逐一跟标号i的位置元素比较，找到更小或更大的元素时，只是把标号存下来，
     * 等一趟比较完成后再把最终标号的元素与标号i的位置元素交换。
     *
     * @param array
     * @param <T>
     */
    public static <T extends Comparable<? super T>> void selectSort(T[] array) {
        T temp;
        int index;
        for (int i = 0; i < array.length - 1; i++) {
            index = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[index].compareTo(array[j]) > 0) {
                    index = j;
                }
            }
            if (index != i) {
                temp = array[i];
                array[i] = array[index];
                array[index] = temp;
            }
        }
    }

    /**
     * 反转排序：
     * 就是以当前序列为基础，把整个顺序翻转过来
     *
     * @param array
     * @param <T>
     */
    public static <T extends Comparable<? super T>> void reverseSort(T[] array) {
        T temp;
        int size = array.length;
        int middle = size / 2;
        int reflectIndex;
        for (int i = 0; i < middle; i++) {
            reflectIndex = size - 1 - i;
            temp = array[i];
            array[i] = array[reflectIndex];
            array[reflectIndex] = temp;
        }
    }

    /**
     * 快速排序：
     * 1.找一个基准，本方法以序列的第一个元素为基准，比基准值小的都在左边序列，比基准值大的都在右边；
     * <p>
     * 2.按基准分割成两份子序列：
     * 2.1首先从最后一个元素依次往前与基准值比较，如果比基准值大或者相等，就继续比较下一个，
     * 直到找到一个比基准值小的值才交换，同时切换比较方向为从前往后与基准值比较；
     * 2.2然后从前往后依次与基准值比较，如果比基准值小或者相等，就继续比较下一个，
     * 直到找到一个比基准值大的值才交换，同时切换比较方向为从后往前与基准值比较；
     * 2.3上述过程直到从前往后的比较索引大于等于从后往前比较的索引，结束第一次循环，
     * 此时，对于基准值来说，左右两边就是有序的了。
     * <p>
     * 3.接着分别递归调用比较左右两边的序列，重复上述的循环。
     *
     * @param array
     * @param low
     * @param high
     * @param <T>
     */
    public static <T extends Comparable<? super T>> void quickSort(T[] array, int low, int high) {
        int forwardIndex = low;
        int backwardIndex = high;
        // 这里选择第一个元素作为基准值
        T pivot = array[low];

        while (forwardIndex < backwardIndex) {
            // 从后往前比较：
            // 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
            while (backwardIndex > forwardIndex && array[backwardIndex].compareTo(pivot) >= 0)
                backwardIndex--;
            if (array[backwardIndex].compareTo(pivot) < 0) {
                T temp = array[backwardIndex];
                array[backwardIndex] = array[forwardIndex];
                array[forwardIndex] = temp;
            }

            // 从前往后比较：
            // 如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置，然后又从后往前比较
            while (backwardIndex > forwardIndex && array[forwardIndex].compareTo(pivot) <= 0)
                forwardIndex++;
            if (array[forwardIndex].compareTo(pivot) > 0) {
                T temp = array[forwardIndex];
                array[forwardIndex] = array[backwardIndex];
                array[backwardIndex] = temp;
            }
            // 此时第一次循环比较结束，关键值的位置已经确定了。
            // 左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }

        // 递归调用
        // 左边序列：从第一个索引位置到关键值索引-1
        if (forwardIndex > low) quickSort(array, low, forwardIndex - 1);
        // 右边序列：从关键值索引+1到最后一个索引位置
        if (backwardIndex < high) quickSort(array, backwardIndex + 1, high);
    }

    /**
     * 两路归并排序：
     *
     * @param array
     * @param low
     * @param high
     * @param <T>
     */
    public static <T extends Comparable<? super T>> void mergeSort(T[] array, int low, int high) {
        if (low < high) {
            int mid = (low + high) / 2;
            mergeSort(array, low, mid);
            mergeSort(array, mid + 1, high);
            //左右归并
            merge(array, low, mid, high);
        }
    }

    private static <T extends Comparable<? super T>> void merge(T[] array, int low, int mid, int high) {
        T[] temp = (T[]) Array.newInstance(array.getClass().getComponentType(), high - low + 1);
        int i = low;
        int j = mid + 1;
        int mergeIndex = 0;

        // 把较小的数先移到新数组中
        while (i <= mid && j <= high) {
            if (array[i].compareTo(array[j]) < 0) {
                temp[mergeIndex++] = array[i++];
            } else {
                temp[mergeIndex++] = array[j++];
            }
        }

        // 把左边剩余的数移入新数组
        while (i <= mid) {
            temp[mergeIndex++] = array[i++];
        }

        // 把右边边剩余的数移入新数组
        while (j <= high) {
            temp[mergeIndex++] = array[j++];
        }

        // 把新数组中的数覆盖原数组
        for (int x = 0; x < temp.length; x++) {
            array[x + low] = temp[x];
        }
    }

}
