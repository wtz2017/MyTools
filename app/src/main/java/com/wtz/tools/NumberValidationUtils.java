package com.wtz.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidationUtils {
	private static boolean isMatch(String regex, String target) {
		if (target == null || target.trim().equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		return matcher.matches();
	}

	public static boolean isFloatIntString(String target) {
		return isFloatString(target) || isIntString(target);
	}

	public static boolean isFloatString(String target) {
		return isMatch("^\\d+[.]\\d+$", target);
	}

	public static boolean isIntString(String target) {
		return isMatch("^\\d+$", target);
	}
	
	public static boolean isComposteOf0Or1(String target) {
		return isMatch("^[0,1]*$", target);
	}
}
