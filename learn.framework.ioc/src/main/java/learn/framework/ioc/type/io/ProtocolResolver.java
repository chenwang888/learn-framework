package learn.framework.ioc.type.io;

import learn.framework.ioc.lang.Nullable;

@FunctionalInterface
public interface ProtocolResolver {
    @Nullable
    Resource resolve(String location, ResourceLoader resourceLoader);
}
