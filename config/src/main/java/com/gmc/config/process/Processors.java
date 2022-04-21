package com.gmc.config.process;

import com.gmc.config.ConfigProperty;
import com.gmc.config.String2ListPreprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Processors {
    protected List<Processor> processors = new ArrayList<>();
    protected List<OneConfigEntryPreProcessor> preProcessors = new ArrayList<>();
    protected List<OneConfigEntryPostProcessor> postProcessors = new ArrayList<>();
    protected List<OneResourceProcessor> oneResourceProcessors = new ArrayList<>();
    protected List<OnePathProcessor> onePathProcessors = new ArrayList<>();
    protected List<SameLevelPathsProcessor> sameLevelPathsProcessors = new ArrayList<>();
    protected List<AllPathsProcessor> allPathsProcessors = new ArrayList<>();
    protected Set<ConfigProperty> processedSameLevelPath = new HashSet<>();

    protected List<List> classification(Processor processor) {
        List<List> result = new ArrayList<>();
        if (processor instanceof OneConfigEntryPreProcessor) {
            result.add(preProcessors);
        }
        if (processor instanceof OneConfigEntryPostProcessor) {
            result.add(postProcessors);
        }
        if (processor instanceof OneResourceProcessor) {
            result.add(oneResourceProcessors);

        }
        if (processor instanceof OnePathProcessor) {
            result.add(onePathProcessors);

        }
        if (processor instanceof SameLevelPathsProcessor) {
            result.add(sameLevelPathsProcessors);

        }
        if (processor instanceof AllPathsProcessor) {
            result.add(allPathsProcessors);

        }
        assert result.size() > 0;
        return result;
    }

    public Processors(List<Processor> processors) {
        assert processors != null;
        this.processors.addAll(processors);
        for (Processor processor : processors) {
            List<List> classifications = classification(processor);
            classifications.forEach(list -> list.add(processor));
        }
    }

    public static Processors create() {
        Processors processors = new Processors(Collections.emptyList());
        processors.addProcessor(new PlaceholderProcessor());
        processors.addProcessor(new String2ListPreprocessor());
        return processors;

    }

    public void addProcessor(Processor processor) {
        List<List> classifications = classification(processor);
        classifications.forEach(list -> list.add(processor));
    }


    public Object preprocessOneConfigEntry(OneConfigEntryProcessContext context) {
        Object object = context.getValue();
        for (OneConfigEntryPreProcessor preprocessor : preProcessors) {
            object = preprocessor.preprocessOneConfigEntry(context);
        }
        return object;
    }

    public void addBefore(Processor processor, Processor target) {
        boolean find = false;
        List<List> classifications = classification(processor);
        for (int i = 0; i < classifications.size(); i++) {
            List list = classifications.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (target.equals(list.get(j))) {
                    list.add(Math.max(j - 1, 0), processor);
                    find = true;
                    break;
                }
            }

        }
        assert find;
    }

    public void addAfter(Processor processor, Processor target) {
        boolean find = false;
        List<List> classifications = classification(processor);
        for (int i = 0; i < classifications.size(); i++) {
            List list = classifications.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (target.equals(list.get(j))) {
                    list.add(j + 1, processor);
                    find = true;
                    break;
                }
            }

        }
        assert find;
    }

    public void processOneResource(ConfigProperty configProperty) {
        oneResourceProcessors.forEach(processor -> processor.processResource(configProperty));
    }

    public void postProcessOneConfigEntry(OneConfigEntryProcessContext context) {
        postProcessors.forEach(postProcessor -> postProcessor.postprocessOneConfigEntry(context));
    }

    public void processOnePath(ConfigProperty configProperty) {
        onePathProcessors.forEach(processor -> processor.processOnePath(configProperty));
    }

    public void processSameLevelPaths(ConfigProperty configProperty) {
        if (!processedSameLevelPath.contains(configProperty)) {
            sameLevelPathsProcessors.forEach(processor -> processor.processSameLevelPaths(configProperty));
            processedSameLevelPath.add(configProperty);
        }
    }

    public void processAllPaths() {
        allPathsProcessors.forEach(processor -> processor.processAllPaths());
    }
}
