package learn.framework.ioc.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    /**
     * 扫描外部依赖 learn-framework.jar 中的class
     * @return 返回 learn-framework.jar 的所在路径
     * @throws UnsupportedEncodingException
     */
    public static String findJarPath() throws UnsupportedEncodingException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();
            for (URL url : urls) {
                String path = URLDecoder.decode(url.getFile(), "UTF-8");
                if (path.endsWith("learn-framework.jar")) {
                    return path;
                }
            }
        }
        return null;
    }

    public static void scanJarFile(List<Class<?>> classes, String jarFilePath) throws IOException, ClassNotFoundException {

        if (jarFilePath != null) {
            try (JarFile jarFile = new JarFile(new File(jarFilePath))) {
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {

                        String className = entry.getName().replace(".class", "").replace("/", ".");

                        Class<?> clazz = Class.forName(className);

                        // 排除接口和注解、枚举
                        if (!clazz.isInterface() && !clazz.isAnnotation() && !clazz.isEnum()){
                            classes.add(clazz);
                        }
                    }
                }
            }
        }

    }

    public static void scanPackage(List<Class<?>> classList, String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String filePath = URLDecoder.decode(resource.getFile(), "UTF-8");

            scanPackageClasses(packageName, filePath, classList);
        }
    }

    private static void scanPackageClasses(String packageName, String packagePath, List<Class<?>> classes) throws ClassNotFoundException {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackageName = packageName + "." + file.getName();
                String subPackagePath = packagePath + "/" + file.getName();
                scanPackageClasses(subPackageName, subPackagePath, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);

                // 排除接口和注解、枚举
                if (!clazz.isInterface() && !clazz.isAnnotation() && !clazz.isEnum()){
                    classes.add(clazz);
                }
            }
        }
    }


}
