package learn.framework.ioc.type.io;

import learn.framework.ioc.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public interface Resource extends InputStreamSource {
    boolean exists();

    default boolean isReadable() {
        return this.exists();
    }

    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }

    URL getURL() throws IOException;

    URI getURI() throws IOException;

    File getFile() throws IOException;

    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(this.getInputStream());
    }

    long contentLength() throws IOException;

    long lastModified() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    @Nullable
    String getFilename();

    String getDescription();
}
