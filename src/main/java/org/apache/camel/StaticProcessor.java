package org.apache.camel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticProcessor implements Processor {
    private final String folder;

    public StaticProcessor(String folder) {
        this.folder = folder;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String path = exchange.getIn().getHeader(Exchange.HTTP_PATH, String.class);

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.isEmpty() || path.endsWith("/")) {
            path += "index.html";
        }

        Path folderPath = Paths.get(folder, path);

        if (Files.isDirectory(folderPath)) {
            folderPath = folderPath.resolve("index.html");
        }

        if (Files.exists(folderPath) && !Files.isDirectory(folderPath)) {
            exchange.getIn().setBody(Files.readString(folderPath));
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            exchange.getIn().setBody("404 Not Found");
        }
    }
}
