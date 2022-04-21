package com.gmc.config;

import com.gmc.config.file.FileLocationResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocationResolvers {
    List<LocationResolver<? extends Resource>> list = new ArrayList<>();

    public LocationResolvers(List<LocationResolver<? extends Resource>> udLocationResolvers) {
        assert udLocationResolvers != null;
        list.addAll(udLocationResolvers);
        list.add(new FileLocationResolver(InnerConfigDefiner.LOCAL_FILE_LISTEN.getKey(), obj -> GlobalParams.getTypeConverters().convert(obj, TypeWrapper.of(obj.getClass()), TypeWrapper.of(Boolean.class))));
    }

    public boolean support(Location location) {
        return list.stream().anyMatch(resolver -> resolver.support(location));
    }

    public List<Resource> resolve(Location location) {
        return list.stream().filter(resolver -> resolver.support(location))
                .findFirst()
                .map(resolver -> resolver.resolve(location))
                .map(resources -> resources.stream().map(resource -> ((Resource) resource)).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    public static void main(String[] args) {


    }


}
