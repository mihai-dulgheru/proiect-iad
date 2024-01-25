package org.apache.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

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
        Path folderPath = Paths.get(folder, path);
        if (Files.exists(folderPath)) {
            exchange.getIn().setBody(Files.readString(folderPath));
        }
    }
}
