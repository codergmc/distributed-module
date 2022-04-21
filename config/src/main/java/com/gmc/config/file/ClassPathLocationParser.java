package com.gmc.config.file;

import com.gmc.config.LocationParser;
import com.gmc.config.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ClassPathLocationParser implements LocationParser<FileLocation> {
    static final Logger LOGGER = LoggerFactory.getLogger(ClassPathLocationParser.class);
    @Override
    public boolean support(String path) {
        try {
            URI uri = new URI(path);
            String scheme = uri.getScheme();
            if (!scheme.equals("classpath")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public FileLocation parse(String path) {
        URI uri = null;
        try {
            uri = new URI(path);
        } catch (URISyntaxException e) {
            LOGGER.error("file path illegal, path:{}", path);
            return new FileLocation();
        }
        QueryStringDecoder decoder = new QueryStringDecoder(path);
        Map<String, List<String>> parameters = decoder.parameters();
        String classpath = uri.getSchemeSpecificPart().substring(2);
        URL resource = Thread.currentThread().getContextClassLoader().getResource(classpath);
        File file = new File(resource.getFile());
        FileLocation fileLocation = new FileLocation(parameters);
        return fileLocation;
    }
}
