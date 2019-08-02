package com.wtz.tools.utils.judgments;

import android.support.annotation.Nullable;

import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * Methods factored out so that they can be emulated differently in GWT.
 *
 * @author Jesse Wilson
 */
final class Platform {
    private static final Logger logger = Logger.getLogger(Platform.class.getName());
    private static final PatternCompiler patternCompiler = loadPatternCompiler();

    private Platform() {}

    /** Calls {@link System#nanoTime()}. */
    static long systemNanoTime() {
        return System.nanoTime();
    }

    static String formatCompact4Digits(double value) {
        return String.format(Locale.ROOT, "%.4g", value);
    }

    static boolean stringIsNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    static CommonPattern compilePattern(String pattern) {
        Preconditions.checkNotNull(pattern);
        return patternCompiler.compile(pattern);
    }

    static boolean usingJdkPatternCompiler() {
        return patternCompiler instanceof JdkPatternCompiler;
    }

    private static PatternCompiler loadPatternCompiler() {
        /*
         * We'd normally use ServiceLoader here, but it hurts Android startup performance. To avoid
         * that, we hardcode the JDK Pattern compiler on Android (and, inadvertently, on App Engine and
         * in Guava, at least for now).
         */
        return new JdkPatternCompiler();
    }

    private static void logPatternCompilerError(ServiceConfigurationError e) {
        logger.log(Level.WARNING, "Error loading regex compiler, falling back to next option", e);
    }

    private static final class JdkPatternCompiler implements PatternCompiler {
        @Override
        public CommonPattern compile(String pattern) {
            return new JdkPattern(Pattern.compile(pattern));
        }
    }
}