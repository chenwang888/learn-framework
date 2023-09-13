package learn.framework.ioc.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

    private final Map<String, Map<String, String>> propertiesMap;

    public PropertiesLoader () {
        propertiesMap = new HashMap<>();
    }

    public void loadProperties(String folderPath) {
        loadPropertiesRecursive(new File(folderPath));
    }

    private void loadPropertiesRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    loadPropertiesRecursive(subFile);
                }
            }
        } else if (file.isFile() && file.getName().endsWith(".properties")) {
            try {
                Properties properties = new Properties();
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);

                // 将属性加载到文件的属性数据中
                Map<String, String> filePropertiesMap = new HashMap<>();
                for (String key : properties.stringPropertyNames()) {
                    String value = properties.getProperty(key);
                    filePropertiesMap.put(key, value);
                }

                // 将文件的属性数据存储到工厂中
                String fileName = file.getName();
                propertiesMap.put(fileName, filePropertiesMap);

                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getProperty(String fileName, String key) {
        Map<String, String> filePropertiesMap = propertiesMap.get(fileName);
        if (filePropertiesMap != null) {
            return filePropertiesMap.get(key);
        }
        return null;
    }
}
