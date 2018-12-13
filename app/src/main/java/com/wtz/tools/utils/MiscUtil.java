package com.wtz.tools.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiscUtil {
    private final static String TAG = MiscUtil.class.getName();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List randomList(List sourceList) {
        if (sourceList == null || sourceList.size() == 0) {
            return sourceList;
        }

        List copyList = new ArrayList();
        copyList.addAll(sourceList);
        List randomList = new ArrayList(copyList.size());
        
        Random random = new Random();
        int randomIndex;
        do {
            randomIndex = Math.abs(random.nextInt(copyList.size()));
            randomList.add(copyList.remove(randomIndex));
        } while (copyList.size() > 0);

        return randomList;
    }

    public static String getErroInfoFromException(Exception e) {
        if (e == null) {
            return "";
        }

        try {
            Writer stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.close();
            return stringWriter.toString();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return e.toString();
    }
    
    public static StringBuilder printFileArray(File[] files) {
		StringBuilder builder = new StringBuilder();
		for (File f : files) {
			builder.append(f).append(";");
		}
		return builder;
	}
}
