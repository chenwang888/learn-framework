package learn.framework.ioc.type.io;

import learn.framework.ioc.lang.Nullable;

public interface ResourceLoader {
    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);

    @Nullable
    ClassLoader getClassLoader();
}
