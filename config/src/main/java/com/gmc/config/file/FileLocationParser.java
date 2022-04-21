package com.gmc.config.file;

import com.gmc.config.LocationParser;
import com.gmc.config.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileLocationParser implements LocationParser<FileLocation> {
    static final Logger LOGGER = LoggerFactory.getLogger(FileLocationParser.class);

    @Override
    public boolean support(String path) {
        try {
            URI uri = new URI(path);
            String scheme = uri.getScheme();
            if (!scheme.equals("file")) {
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
        if (path.contains("*")) {
            String filePath = getFilePath(uri);
            List<File> location = findLocation(filePath);
            return new FileLocation(location, parameters);


        } else {
            String filePath = getFilePath(uri);
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                LOGGER.error("file path not exist or is directory ,path:{}", path);
                return new FileLocation();
            }
            return new FileLocation(file, parameters);


        }
    }

    private String getFilePath(URI uri) {
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        return schemeSpecificPart.substring(2);
    }

    private List<File> findLocation(String filePath) {
        List<File> result = new ArrayList<>();
        if (checkError(filePath)) {
            return result;
        } else {

            resolveNormalPath(filePath, null, result);
            return result;

        }
    }

    private void resolveAsterisk(String match, File parentPathToSearch, List<File> result) {
        String newMatch = match.substring(2);
        File[] files = parentPathToSearch.listFiles();
        if (files != null) {
            for (File file : files) {
                if (match.charAt(3) != '*') {
                    resolveNormalPath(newMatch, file, result);
                } else {
                    if (match.length() > 4) {
                        if (match.charAt(4) == '*') {
                            resolveDoubleAsterisk(newMatch, file, result, true);
                        } else {
                            resolveAsterisk(newMatch, file, result);
                        }
                    }
                }
            }
        }

    }

    private void resolveDoubleAsterisk(String match, File parentPathToSearch, List<File> result, boolean first) {
        String newMatch = match.substring(3);
        File[] files = parentPathToSearch.listFiles();
        if (first) {
            addResult(newMatch, parentPathToSearch, result);
        }
        if (files != null) {
            for (File file : files) {
                if (match.charAt(4) == '*') {
                    //todo
                    throw new IllegalArgumentException();
                } else {
                    resolveNormalPath(newMatch, file, result);
                }
                resolveDoubleAsterisk(match, file, result, false);
            }
        }
    }

    private void addResult(String match, File parentPathToSearch, List<File> result) {
        File file;
        if (parentPathToSearch != null) {
            file = new File(parentPathToSearch, match.substring(1));
        } else {
            //todo
            throw new IllegalArgumentException();
        }
        if (file != null && file.exists() && !file.isDirectory()) {
            result.add(file);
        }
    }

    /**
     * 解析 非 /* /** 开头的正则
     *
     * @param match              匹配的正则
     * @param parentPathToSearch
     * @param result             返回匹配到的file
     */
    private void resolveNormalPath(String match, File parentPathToSearch, List<File> result) {

        if (match != null && match.length() > 0) {
            int i = match.indexOf("*");
            File file = null;

            if (i < 0) {
                addResult(match, parentPathToSearch, result);
            } else {
                String childPath = match.substring(0, i);
                if (parentPathToSearch != null) {
                    file = new File(parentPathToSearch, childPath);
                } else {
                    file = new File(childPath);
                }
                if (file.exists() && file.isDirectory()) {
                    if (match.charAt(i + 1) == '*') {

                        resolveDoubleAsterisk(match.substring(i - 1), file, result, true);
                    } else {
                        resolveAsterisk(match.substring(i - 1), file, result);

                    }
                }

            }


        }
    }


    private boolean checkError(String filePath) {
        int i = filePath.indexOf("*");
        if (i == 0) {
            //todo
            LOGGER.error("file path");
            return true;
        }
        if (filePath.lastIndexOf("*") == filePath.length() - 1) {
            //todo
            LOGGER.error("file path");
            return true;
        }
        return false;
    }

}
