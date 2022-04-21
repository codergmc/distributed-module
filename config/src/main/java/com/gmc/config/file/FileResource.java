package com.gmc.config.file;

import com.gmc.config.ParameterResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class FileResource extends ParameterResource {
    protected File file;

    /**
     *
     * @param file
     * @param params not null
     */
    public FileResource(File file, Map<String, List<String>> params) {
        super(params);
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String getNameSpace() {
        return file.getParent();
    }

    @Override
    public String getResourceName() {
        return file.getName();
    }

    @Override
    public String getProfile() {
        return "";
    }


    public File getFile() {
        return file;
    }
}
