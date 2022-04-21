package com.gmc.config;

import com.gmc.config.file.ClassPathLocationParser;
import com.gmc.config.file.FileLocationParser;
import com.gmc.core.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationParsers  {
    List<LocationParser<? extends Location>> list = new ArrayList<>();
    static final Logger LOGGER = LoggerFactory.getLogger(LocationParsers.class);

    public LocationParsers(LocationParser<? extends Location>... udLocationParsers) {
        this(Arrays.asList(udLocationParsers));
    }

    public LocationParsers(List<LocationParser<? extends Location>> udLocationParsers) {
        assert udLocationParsers != null;
        list.addAll(udLocationParsers);
        list.add(new FileLocationParser());
        list.add(new ClassPathLocationParser());
    }

    public Location parse(String path) {
        return list.stream().filter(parser -> parser.support(path))
                .findFirst()
                .map(parser -> parser.parse(path))
                .orElseGet(
                        () -> {
                            LOGGER.error(LogUtils.format("un support path:{}", path));
                            return null;
                        });
    }
}
