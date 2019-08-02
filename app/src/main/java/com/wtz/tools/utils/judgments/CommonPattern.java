package com.wtz.tools.utils.judgments;

/**
 * The subset of the {@link java.util.regex.Pattern} API which is used by this package, and also
 * shared with the {@code re2j} library. For internal use only. Please refer to the {@code Pattern}
 * javadoc for details.
 */
abstract class CommonPattern {
    abstract CommonMatcher matcher(CharSequence t);

    abstract String pattern();

    abstract int flags();

    // Re-declare these as abstract to force subclasses to override.
    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);
}
