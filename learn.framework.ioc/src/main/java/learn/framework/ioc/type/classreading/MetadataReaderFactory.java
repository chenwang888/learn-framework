package learn.framework.ioc.type.classreading;

import learn.framework.ioc.type.io.Resource;

import java.io.IOException;

public interface MetadataReaderFactory {
    MetadataReader getMetadataReader(String className) throws IOException;

    MetadataReader getMetadataReader(Resource resource) throws IOException;
}
