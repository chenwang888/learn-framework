package learn.framework.ioc.util;

import java.util.Objects;

public class PackageUtil {

    public static String replacePoint(String packageName) {
        Objects.requireNonNull(packageName, "@ComponentScan value is null");
        return packageName.replace(".", "/");
    }

    public static String fullyQualifiedName(String fileName) {

        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
        return className.replace("\\", ".");
    }

}
