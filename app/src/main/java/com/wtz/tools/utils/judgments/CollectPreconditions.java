package com.wtz.tools.utils.judgments;

/**
 * Precondition checks useful in collection implementations.
 */
public final class CollectPreconditions {

    public static void checkEntryNotNull(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("null key in entry: null=" + value);
        } else if (value == null) {
            throw new NullPointerException("null value in entry: " + key + "=null");
        }
    }

    public static int checkNonnegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    public static void checkPositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive but was: " + value);
        }
    }

}
