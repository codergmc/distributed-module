package com.gmc.config.file;

import com.gmc.config.ParameterLocation;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileLocation extends ParameterLocation {
    private List<File> fileList;

    public FileLocation() {
        this(Collections.emptyMap());
    }

    public FileLocation(Map<String, List<String>> params) {
        super(params);
        this.fileList = Collections.emptyList();
    }

    public FileLocation(File file, Map<String, List<String>> params) {
        this(Collections.singletonList(file), params);
    }

    public FileLocation(List<File> fileList, Map<String, List<String>> params) {
        super(params);
        this.fileList = fileList;
    }

    public List<File> getFileList() {
        return fileList;
    }



}
