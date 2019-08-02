package com.wtz.tools.utils.judgments;

/**
 * The subset of the {@link java.util.regex.Matcher} API which is used by this package, and also
 * shared with the {@code re2j} library. For internal use only. Please refer to the {@code Matcher}
 * javadoc for details.
 */
abstract class CommonMatcher {
    abstract boolean matches();

    abstract boolean find();

    abstract boolean find(int index);

    abstract String replaceAll(String replacement);

    abstract int end();

    abstract int start();
}