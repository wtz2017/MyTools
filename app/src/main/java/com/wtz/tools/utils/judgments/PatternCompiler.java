package com.wtz.tools.utils.judgments;

/**
 * Pluggable interface for compiling a regex pattern. By default this package uses the
 * {@code java.util.regex} library, but an alternate implementation can be supplied
 * using the {@link java.util.ServiceLoader} mechanism.
 */
interface PatternCompiler {
    /**
     * Compiles the given pattern.
     *
     * @throws IllegalArgumentException if the pattern is invalid
     */
    CommonPattern compile(String pattern);
}
