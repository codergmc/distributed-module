package com.gmc.config;

import com.gmc.config.file.FileResource;
import com.gmc.core.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileChangeNotifyParameterResource extends FileResource implements ChangeNotifyResource<FileChangeNotifyParameterResource> {
    static final Logger LOGGER = LoggerFactory.getLogger(FileChangeNotifyParameterResource.class);
    protected List<ResourceChangeListener<FileChangeNotifyParameterResource>> listeners = new ArrayList<>();
    protected ScheduledExecutorService executorService;
    protected FileTime lastModifiedTime;
    protected ConfigProperty topConfigProperty;
    protected long fileListenCycleTime;

    public FileChangeNotifyParameterResource(File file, Map<String, List<String>> params) {
        super(file, params);
        this.executorService = GlobalParams.INSTANCE.getParam(Config.EXECUTORS);
        lastModifiedTime = getLastModifiedTime();
        topConfigProperty = GlobalParams.INSTANCE.getParam(Config.HEAD_CONFIG_PROPERTY);
        ConfigValue<Long> configValue = topConfigProperty.getConfigValue(InnerConfigDefiner.LOCAL_FILE_LISTEN_CYCLE);
        fileListenCycleTime = configValue.getValue();
        executorService.scheduleWithFixedDelay(() -> {
            FileTime lastModifiedTime = getLastModifiedTime();
            if (lastModifiedTime != null) {
                if (lastModifiedTime.equals(FileChangeNotifyParameterResource.this.lastModifiedTime)) {
                    listeners.forEach(listener -> {
                        try {
                            listener.changeNotify(new ChangeNotifyContext<>(FileChangeNotifyParameterResource.this));
                        } catch (IOException e) {
                            LOGGER.warn(LogUtils.format("file notify error,file:{},listener:{}", file.getAbsolutePath(), listener.getClass()), e);
                        }
                    });
                }
            }
            FileChangeNotifyParameterResource.this.lastModifiedTime = lastModifiedTime;

        }, new Random().nextInt((int) fileListenCycleTime), fileListenCycleTime, TimeUnit.MILLISECONDS);
    }

    private FileTime getLastModifiedTime() {
        try {
            return Files.getLastModifiedTime(file.getAbsoluteFile().toPath());
        } catch (IOException e) {
            LOGGER.warn(LogUtils.format("path:{}", file.getAbsoluteFile()), e);
        }
        return null;

    }

    @Override
    public void addChangeNotifyListener(ResourceChangeListener<FileChangeNotifyParameterResource> resourceChangeListener) {
        listeners.add(resourceChangeListener);
    }

}
