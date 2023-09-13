package learn.framework.ioc.type.classreading;

import learn.framework.ioc.lang.Nullable;
import learn.framework.ioc.type.ClassMetadata;
import learn.framework.ioc.type.io.Resource;

import java.io.IOException;

final class SimpleMetadataReader implements MetadataReader {
    private static final int PARSING_OPTIONS = 7;
    private final Resource resource;

    SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        this.resource = resource;
    }

    public Resource getResource() {
        return this.resource;
    }

    public ClassMetadata getClassMetadata() {
        return null;
    }
}
