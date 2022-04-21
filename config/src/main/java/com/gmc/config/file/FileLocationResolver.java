package com.gmc.config.file;

import com.gmc.config.FileChangeNotifyParameterResource;
import com.gmc.config.Location;
import com.gmc.config.LocationResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FileLocationResolver implements LocationResolver<FileResource> {
    protected String key;
    protected Function<Object, Boolean> needListenChange;

    public FileLocationResolver(String key, Function<Object, Boolean> needListenChange) {
        this.key = key;
        this.needListenChange = needListenChange;
    }

    @Override
    public boolean support(Location location) {
        return location instanceof FileLocation;
    }

    @Override
    public List<FileResource> resolve(Location location) {
        FileLocation fileLocation = ((FileLocation) location);
        List<FileResource> list = new ArrayList<>();
        for (File file : fileLocation.getFileList()) {
            boolean listenChange = Optional.ofNullable(fileLocation.getParams().get(key)).map(values -> values.stream().anyMatch(needListenChange::apply)).orElse(false);
            if (listenChange) {
                list.add(new FileChangeNotifyParameterResource(file, fileLocation.getParams()));
            } else {
                list.add(new FileResource(file, fileLocation.getParams()));
            }
        }
        return list;
    }


}
