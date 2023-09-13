package learn.framework.ioc.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ResourcePathUtil {

    public static String getResourcePath() {
        String path = ResourcePathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (path.endsWith(".jar")) {
            // 在打包后运行的情况下，获取jar包所在的目录
            path = new File(path).getParent();
        } else {
            // 在开发时的情况下，获取resources目录下的路径
            path = ResourcePathUtil.class.getClassLoader().getResource("").getPath();
        }

        return path;
    }
}
