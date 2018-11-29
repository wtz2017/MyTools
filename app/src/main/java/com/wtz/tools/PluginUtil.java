package com.wtz.tools;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class PluginUtil {

    private static final String TAG = "PluginUtil";

    private static String sNativeLibraryDir;
    private static String[] sSupportedABINames;

    /**
     *
     * @param classLoader 必须是与调用System.loadLibrary()的类的classLoader相同
     * @param libPath
     */
    public static void expandNativeLibraryDirectory(ClassLoader classLoader, File libPath) {
        List<File> libPathList = new ArrayList<File>();
        libPathList.add(libPath);
        expandNativeLibraryDirectories(classLoader, libPathList);
    }

    public static void expandNativeLibraryDirectories(ClassLoader classLoader, List<File> libPath) {
        int v = Build.VERSION.SDK_INT;
        if (v < 14) {
            V9_13.expandNativeLibraryDirectories(classLoader, libPath);
        } else if (v < 23) {
            V14_22.expandNativeLibraryDirectories(classLoader, libPath);
        } else if (v < 26) {
            V23_25.expandNativeLibraryDirectories(classLoader, libPath);
        } else {
            V26_.expandNativeLibraryDirectories(classLoader, libPath);
        }
    }

    private static final class V9_13 {

        private static Field sDexClassLoader_mFiles_field;
        private static Field sDexClassLoader_mPaths_field;
        private static Field sDexClassLoader_mZips_field;
        private static Field sDexClassLoader_mDexs_field;
        private static Field sPathClassLoader_libraryPathElements_field;

        public static boolean expandDexPathList(ClassLoader cl,
                                                String[] dexPaths, DexFile[] dexFiles) {
            ZipFile[] zips = null;
            try {
                /*
                 * see https://android.googlesource.com/platform/libcore/+/android-2.3_r1/dalvik/src/main/java/dalvik/system/DexClassLoader.java
                 */
                if (sDexClassLoader_mFiles_field == null) {
                    sDexClassLoader_mFiles_field = getDeclaredField(cl.getClass(), "mFiles");
                    sDexClassLoader_mPaths_field = getDeclaredField(cl.getClass(), "mPaths");
                    sDexClassLoader_mZips_field = getDeclaredField(cl.getClass(), "mZips");
                    sDexClassLoader_mDexs_field = getDeclaredField(cl.getClass(), "mDexs");
                }
                if (sDexClassLoader_mFiles_field == null
                        || sDexClassLoader_mPaths_field == null
                        || sDexClassLoader_mZips_field == null
                        || sDexClassLoader_mDexs_field == null) {
                    return false;
                }

                int N = dexPaths.length;
                Object[] files = new Object[N];
                Object[] paths = new Object[N];
                zips = new ZipFile[N];
                for (int i = 0; i < N; i++) {
                    String path = dexPaths[i];
                    files[i] = new File(path);
                    paths[i] = path;
                    zips[i] = new ZipFile(path);
                }

                expandArray(cl, sDexClassLoader_mFiles_field, files, true);
                expandArray(cl, sDexClassLoader_mPaths_field, paths, true);
                expandArray(cl, sDexClassLoader_mZips_field, zips, true);
                expandArray(cl, sDexClassLoader_mDexs_field, dexFiles, true);
            } catch (Exception e) {
                e.printStackTrace();
                if (zips != null) {
                    for (ZipFile zipFile : zips) {
                        try {
                            zipFile.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return false;
            }
            return true;
        }

        public static void expandNativeLibraryDirectories(ClassLoader classLoader,
                                                          List<File> libPaths) {
            if (sPathClassLoader_libraryPathElements_field == null) {
                sPathClassLoader_libraryPathElements_field = getDeclaredField(
                        classLoader.getClass(), "libraryPathElements");
            }
            List<String> paths = getValue(sPathClassLoader_libraryPathElements_field, classLoader);
            if (paths == null) return;
            for (File libPath : libPaths) {
                paths.add(0, libPath.getAbsolutePath() + File.separator);
            }
        }
    }

    private static class V14_ { // API 14 and upper

        // DexPathList
        protected static Field sPathListField;
        private static Constructor sDexElementConstructor;
        private static Class sDexElementClass;
        private static Field sDexElementsField;

        public static boolean expandDexPathList(ClassLoader cl,
                                                String[] dexPaths, DexFile[] dexFiles) {
            try {
                int N = dexPaths.length;
                Object[] elements = new Object[N];
                for (int i = 0; i < N; i++) {
                    String dexPath = dexPaths[i];
                    File pkg = new File(dexPath);
                    DexFile dexFile = dexFiles[i];
                    elements[i] = makeDexElement(pkg, dexFile);
                }

                fillDexPathList(cl, elements);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * Make dex element
         *
         * @param pkg     archive android package with any file extensions
         * @param dexFile
         * @return dalvik.system.DexPathList$Element
         * @see <a href="https://android.googlesource.com/platform/libcore-snapshot/+/ics-mr1/dalvik/src/main/java/dalvik/system/DexPathList.java">DexPathList.java</a>
         */
        private static Object makeDexElement(File pkg, DexFile dexFile) throws Exception {
            return makeDexElement(pkg, false, dexFile);
        }

        protected static Object makeDexElement(File dir) throws Exception {
            return makeDexElement(dir, true, null);
        }

        private static Object makeDexElement(File pkg, boolean isDirectory, DexFile dexFile) throws Exception {
            if (sDexElementClass == null) {
                sDexElementClass = Class.forName("dalvik.system.DexPathList$Element");
            }
            if (sDexElementConstructor == null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    sDexElementConstructor = sDexElementClass.getConstructor(new Class[]{DexFile.class, File.class});
                } else {
                    sDexElementConstructor = sDexElementClass.getConstructors()[0];
                }
            }
            Class<?>[] types = sDexElementConstructor.getParameterTypes();
            switch (types.length) {
                case 3:
                    if (types[1].equals(ZipFile.class)) {
                        // Element(File apk, ZipFile zip, DexFile dex)
                        ZipFile zip;
                        try {
                            zip = new ZipFile(pkg);
                        } catch (IOException e) {
                            throw e;
                        }
                        try {
                            return sDexElementConstructor.newInstance(pkg, zip, dexFile);
                        } catch (Exception e) {
                            zip.close();
                            throw e;
                        }
                    } else {
                        // Element(File apk, File zip, DexFile dex)
                        return sDexElementConstructor.newInstance(pkg, pkg, dexFile);
                    }
                case 2:
                    if (Build.VERSION.SDK_INT >= 26) {
                        //Only SDK >= 26
                        return sDexElementConstructor.newInstance(dexFile, pkg);
                    }
                case 4:
                default:
                    // Element(File apk, boolean isDir, File zip, DexFile dex)
                    if (isDirectory) {
                        return sDexElementConstructor.newInstance(pkg, true, null, null);
                    } else {
                        return sDexElementConstructor.newInstance(pkg, false, pkg, dexFile);
                    }
            }
        }

        private static void fillDexPathList(ClassLoader cl, Object[] elements)
                throws NoSuchFieldException, IllegalAccessException {
            if (sPathListField == null) {
                sPathListField = getDeclaredField(DexClassLoader.class.getSuperclass(), "pathList");
            }
            Object pathList = sPathListField.get(cl);
            if (sDexElementsField == null) {
                sDexElementsField = getDeclaredField(pathList.getClass(), "dexElements");
            }
            expandArray(pathList, sDexElementsField, elements, true);
        }

        public static void removeDexPathList(ClassLoader cl, int deleteIndex) {
            try {
                if (sPathListField == null) {
                    sPathListField = getDeclaredField(DexClassLoader.class.getSuperclass(), "pathList");
                }
                Object pathList = sPathListField.get(cl);
                if (sDexElementsField == null) {
                    sDexElementsField = getDeclaredField(pathList.getClass(), "dexElements");
                }
                sliceArray(pathList, sDexElementsField, deleteIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class V14_22 extends V14_ {

        protected static Field sDexPathList_nativeLibraryDirectories_field;

        public static void expandNativeLibraryDirectories(ClassLoader classLoader,
                                                          List<File> libPaths) {
            if (sPathListField == null) {
                sPathListField = getDeclaredField(DexClassLoader.class.getSuperclass(), "pathList");
            }

            Object pathList = getValue(sPathListField, classLoader);
            Log.d(TAG, "V14_22 sPathListField=" + sPathListField + ", pathList=" + pathList);
            if (pathList == null) return;

            if (sDexPathList_nativeLibraryDirectories_field == null) {
                sDexPathList_nativeLibraryDirectories_field = getDeclaredField(
                        pathList.getClass(), "nativeLibraryDirectories");
                Log.d(TAG, "V14_22 nativeLibraryDirectories_field=" + sDexPathList_nativeLibraryDirectories_field);
                if (sDexPathList_nativeLibraryDirectories_field == null) return;
            }

            try {
                // File[] nativeLibraryDirectories
                Object[] paths = libPaths.toArray();
                expandArray(pathList, sDexPathList_nativeLibraryDirectories_field, paths, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class V23_25 extends V14_22 {

        protected static Field sDexPathList_nativeLibraryPathElements_field;

        public static void expandNativeLibraryDirectories(ClassLoader classLoader,
                                                          List<File> libPaths) {
            if (sPathListField == null) {
                sPathListField = getDeclaredField(DexClassLoader.class.getSuperclass(), "pathList");
            }

            Object pathList = getValue(sPathListField, classLoader);
            if (pathList == null) return;

            if (sDexPathList_nativeLibraryDirectories_field == null) {
                sDexPathList_nativeLibraryDirectories_field = getDeclaredField(
                        pathList.getClass(), "nativeLibraryDirectories");
                if (sDexPathList_nativeLibraryDirectories_field == null) return;
            }

            try {
                // List<File> nativeLibraryDirectories
                List<File> paths = getValue(sDexPathList_nativeLibraryDirectories_field, pathList);
                if (paths == null) return;
                paths.addAll(libPaths);

                // Element[] nativeLibraryPathElements
                if (sDexPathList_nativeLibraryPathElements_field == null) {
                    sDexPathList_nativeLibraryPathElements_field = getDeclaredField(
                            pathList.getClass(), "nativeLibraryPathElements");
                }
                if (sDexPathList_nativeLibraryPathElements_field == null) return;

                int N = libPaths.size();
                Object[] elements = new Object[N];
                for (int i = 0; i < N; i++) {
                    Object dexElement = makeDexElement(libPaths.get(i));
                    elements[i] = dexElement;
                }

                expandArray(pathList, sDexPathList_nativeLibraryPathElements_field, elements, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final class V26_ extends V23_25 {

        private static Constructor sNativeLibraryElementConstructor;
        private static Class sNativeLibraryElementClass;
        private static Field sNativeLibrarysField;

        /**
         * <a href="https://android.googlesource.com/platform/libcore/+/android-o-preview-3/dalvik/src/main/java/dalvik/system/DexPathList.java">DexPathList.java</>
         *
         * @param libraryDir
         * @return
         * @throws Exception
         */
        private static Object makeNativeLibraryElement(File libraryDir) throws Exception {
            if (sNativeLibraryElementClass == null) {
                sNativeLibraryElementClass = Class.forName("dalvik.system.DexPathList$NativeLibraryElement");
            }
            if (sNativeLibraryElementConstructor == null) {
                sNativeLibraryElementConstructor = sNativeLibraryElementClass.getConstructors()[0];
                sNativeLibraryElementConstructor.setAccessible(true);
            }
            Class<?>[] types = sNativeLibraryElementConstructor.getParameterTypes();
            switch (types.length) {
                case 1:
                    return sNativeLibraryElementConstructor.newInstance(libraryDir);
                case 2:
                default:
                    return sNativeLibraryElementConstructor.newInstance(libraryDir, null);
            }
        }

        public static void expandNativeLibraryDirectories(ClassLoader classLoader,
                                                          List<File> libPaths) {
            if (sPathListField == null) {
                sPathListField = getDeclaredField(DexClassLoader.class.getSuperclass(), "pathList");
            }

            Object pathList = getValue(sPathListField, classLoader);
            if (pathList == null) return;

            if (sDexPathList_nativeLibraryDirectories_field == null) {
                sDexPathList_nativeLibraryDirectories_field = getDeclaredField(
                        pathList.getClass(), "nativeLibraryDirectories");
                if (sDexPathList_nativeLibraryDirectories_field == null) return;
            }

            try {
                // List<File> nativeLibraryDirectories
                List<File> paths = getValue(sDexPathList_nativeLibraryDirectories_field, pathList);
                if (paths == null) return;
                paths.addAll(libPaths);

                // NativeLibraryElement[] nativeLibraryPathElements
                if (sDexPathList_nativeLibraryPathElements_field == null) {
                    sDexPathList_nativeLibraryPathElements_field = getDeclaredField(
                            pathList.getClass(), "nativeLibraryPathElements");
                }
                if (sDexPathList_nativeLibraryPathElements_field == null) return;

                int N = libPaths.size();
                Object[] elements = new Object[N];
                for (int i = 0; i < N; i++) {
                    Object dexElement = makeNativeLibraryElement(libPaths.get(i));
                    elements[i] = dexElement;
                }

                expandArray(pathList, sDexPathList_nativeLibraryPathElements_field, elements, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static <T> T getValue(Field field, Object target) {
        if (field == null) {
            return null;
        }

        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add elements to Object[] with reflection
     *
     * @param target
     * @param arrField
     * @param extraElements
     * @param push          true=push to array head, false=append to array tail
     * @throws IllegalAccessException
     * @see <a href="https://github.com/casidiablo/multidex/blob/publishing/library/src/android/support/multidex/MultiDex.java">MultiDex</a>
     */
    private static void expandArray(Object target, Field arrField,
                                    Object[] extraElements, boolean push)
            throws IllegalAccessException {
        Object[] original = (Object[]) arrField.get(target);
        Object[] combined = (Object[]) Array.newInstance(
                original.getClass().getComponentType(), original.length + extraElements.length);
        if (push) {
            System.arraycopy(extraElements, 0, combined, 0, extraElements.length);
            System.arraycopy(original, 0, combined, extraElements.length, original.length);
        } else {
            System.arraycopy(original, 0, combined, 0, original.length);
            System.arraycopy(extraElements, 0, combined, original.length, extraElements.length);
        }
        arrField.set(target, combined);
    }

    private static void sliceArray(Object target, Field arrField, int deleteIndex)
            throws IllegalAccessException {
        Object[] original = (Object[]) arrField.get(target);
        if (original.length == 0) return;

        Object[] sliced = (Object[]) Array.newInstance(
                original.getClass().getComponentType(), original.length - 1);
        if (deleteIndex > 0) {
            // Copy left elements
            System.arraycopy(original, 0, sliced, 0, deleteIndex);
        }
        int rightCount = original.length - deleteIndex - 1;
        if (rightCount > 0) {
            // Copy right elements
            System.arraycopy(original, deleteIndex + 1, sliced, deleteIndex, rightCount);
        }
        arrField.set(target, sliced);
    }

    private static Field getDeclaredField(Class cls, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static boolean is64bit(Context context) {
        if (sNativeLibraryDir == null) {
            sNativeLibraryDir = context.getApplicationInfo().nativeLibraryDir;
        }
        Log.d(TAG, "sNativeLibraryDir:" + sNativeLibraryDir);
        return sNativeLibraryDir != null && sNativeLibraryDir.contains("64");
    }

    public static String getUsableAbi(Context context, String[] soAbis) {
        if (context == null || soAbis == null || soAbis.length == 0) {
            return null;
        }

        if (sSupportedABINames == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                // Cause we stub all the bundle(*.so) in host, if the host ABI is something
                // 32(64) bit, and then System.loadLibrary cannot accept 64(32) bit ABIs.
                // So we had to choose the related ABI as host.
                // FIXME: any solution?
                if (is64bit(context)) {
                    sSupportedABINames = Build.SUPPORTED_64_BIT_ABIS;
                } else {
                    sSupportedABINames = Build.SUPPORTED_32_BIT_ABIS;
                }
            } else if (Build.CPU_ABI2.equals(Build.UNKNOWN)) {
                sSupportedABINames = new String[]{Build.CPU_ABI};
            } else {
                sSupportedABINames = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        }

        int M = sSupportedABINames.length;
        int N = soAbis.length;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (sSupportedABINames[i].equalsIgnoreCase(soAbis[j])) {
                    return soAbis[j];
                }
            }
        }

        return null;
    }

}
