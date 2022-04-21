package com.gmc.config.process;

import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigValue;
import com.gmc.config.GlobalParams;
import com.gmc.core.LogUtils;
import com.gmc.config.TypeWrapper;
import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlaceholderProcessor extends LazySupportOneConfigEntryPreProcessor implements AllPathsProcessor {
    static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderProcessor.class);
    protected List<MatchItem> curMatchItemList;
    protected Set<ConfigValue<?>> unResolvePlaceholderSet = new LinkedHashSet<>();

    @Override
    protected boolean needLazy(OneConfigEntryProcessContext oneConfigEntryProcessContext) {
        Object value = oneConfigEntryProcessContext.getValue();
        curMatchItemList = null;
        if (value instanceof String) {
            String s = (String) value;
            int startIndex = 0;
            MatchItem matchItem = null;
            while (startIndex < s.length()) {
                try {
                    matchItem = getMatch(startIndex, s);
                } catch (UnMatchException e) {
                    LOGGER.error(LogUtils.format("\"${\",\"}\" not match, config key:{}", oneConfigEntryProcessContext.getConfigKey().getKey()));
                    throw new IllegalArgumentException();
                }
                if (matchItem == null) {
                    break;
                }
                if (curMatchItemList == null) {
                    curMatchItemList = new ArrayList<>();
                }
                curMatchItemList.add(matchItem);
                startIndex = matchItem.endIndex;

            }
            if (matchItem != null) {
                unResolvePlaceholderSet.add(oneConfigEntryProcessContext.getConfigValue());
                oneConfigEntryProcessContext.getConfigValue().setManualGenerate(true);
                return true;

            }


        }
        return false;
    }

    @Override
    public Object preprocessOneConfigEntry0(OneConfigEntryProcessContext oneConfigEntryProcessContext) {
        List<MatchItem> matchItems = curMatchItemList;
        if (matchItems == null) {
            return oneConfigEntryProcessContext.getValue();
        }

        ConfigProperty headConfigProperty = GlobalParams.getHeadConfigProperty();
        ConfigDefiner configDefiner = GlobalParams.getConfigDefiner();
        TypeConverters converters = GlobalParams.getTypeConverters();
        String value = (String) oneConfigEntryProcessContext.getValue();
        StringBuilder result = new StringBuilder();

        for (MatchItem matchItem : matchItems) {
            String matchString = matchItem.matchString;
            ConfigKey<?> matchConfigKey = configDefiner.getConfigKey(matchString);
            ConfigValue<?> matchConfigValue = headConfigProperty.getConfigValue(matchConfigKey);
            boolean isEnd = matchItem.equals(matchItems.get(matchItems.size() - 1));
            replaceMatchString(oneConfigEntryProcessContext.getConfigKey(), result, value, matchItem, matchConfigValue, converters, isEnd);
        }

        return result.toString();


    }


    private MatchItem getMatch(int startIndex, String matchString) throws UnMatchException {
        int placeholderStartIndex = match(startIndex, matchString, "${");
        if (placeholderStartIndex < 0) {
            return null;
        }
        int placeholderEndIndex = match(placeholderStartIndex, matchString, "}");
        if (placeholderEndIndex < 0) {
            throw new UnMatchException();
        }
        String placeholderString = matchString.substring(placeholderStartIndex + 2, placeholderEndIndex);
        return new MatchItem(placeholderStartIndex, placeholderEndIndex + 1, placeholderString);

    }

    private int match(int startIndex, String matchString, String placeholderString) {
        return matchString.indexOf(placeholderString, startIndex);
    }


    @Override
    public void processAllPaths() {
        Set<ConfigValue<?>> set = new LinkedHashSet<>();
        set.addAll(unResolvePlaceholderSet);
        for (ConfigValue<?> configValue : set) {
            configValue.manualGenerateValue();
            unResolvePlaceholderSet.remove(configValue);
        }

    }

    private void replaceMatchString(ConfigKey<?> configKey, StringBuilder result, String value, MatchItem matchItem, ConfigValue<?> matchConfigValue, TypeConverters typeConverters, boolean isEnd) {
        if (result.length() == 0) {
            result.append(value, 0, matchItem.beginIndex);
        }
        if (matchConfigValue == null) {
            return;
        }
        if (matchConfigValue.isManualGenerate()) {
            matchConfigValue.generateValue();
            if (!unResolvePlaceholderSet.remove(matchConfigValue.getConfigKey())) {
                LOGGER.error(LogUtils.format("cycle reference:{},{}", configKey.getKey(), matchConfigValue.getConfigKey().getKey()));
                throw new IllegalArgumentException();
            }
        }
        TypeConverter.ConvertSupportResult support = typeConverters.support(matchConfigValue.getConfigKey().getValueType(), TypeWrapper.of(String.class));
        if (support.isSupport()) {
            result.append((String) support.getTypeConverter().convert(matchConfigValue.getValue(), matchConfigValue.getConfigKey().getValueType(), TypeWrapper.of(String.class)));
        } else {
            LOGGER.error(LogUtils.format("config key:{}, reference key:{} can not convert string", configKey.getKey(), matchConfigValue.getConfigKey().getKey()));
            throw new IllegalArgumentException();
        }
        if (isEnd && matchItem.endIndex < value.length()) {
            result.append(value, matchItem.endIndex, value.length());
        }

    }

    static class UnMatchException extends Exception {

    }

    static class MatchItem {
        int beginIndex;
        int endIndex;
        String matchString;

        public MatchItem(int beginIndex, int endIndex, String matchString) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.matchString = matchString;
        }
    }
}
