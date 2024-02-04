package org.apache.camel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticProcessor implements Processor {
    private final Path folderPath;

    public StaticProcessor(String folder) {
        this.folderPath = Paths.get(folder).toAbsolutePath().normalize();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String path = (String) exchange.getIn().getHeader(Exchange.HTTP_PATH);

        path = path.startsWith("/") ? path.substring(1) : path;

        Path resolvedPath = folderPath.resolve(path).normalize();

        if (!resolvedPath.startsWith(folderPath)) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
            exchange.getIn().setBody("403 Forbidden");
            return;
        }

        if (Files.isDirectory(resolvedPath)) {
            resolvedPath = resolvedPath.resolve("index.html");
        }

        if (Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath)) {
            exchange.getIn().setBody(Files.readString(resolvedPath));
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody("404 Not Found");
        }
    }
}
