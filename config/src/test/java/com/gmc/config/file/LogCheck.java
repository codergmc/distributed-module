package com.gmc.config.file;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogCheck {

    public static LogMatch check(Class<?> c) {
        Logger logger = LoggerFactory.getLogger(c);
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger loggerImpl = (ch.qos.logback.classic.Logger) logger;
            MemAppender memAppender = new MemAppender();
            memAppender.start();
            loggerImpl.addAppender(memAppender);
            return new LogMatch(loggerImpl, memAppender);

        }
        throw new IllegalArgumentException("only support logback");
    }

    static class LogMatch {
        private ch.qos.logback.classic.Logger logger;
        private MemAppender memAppender;

        public LogMatch(ch.qos.logback.classic.Logger logger, MemAppender memAppender) {
            this.logger = logger;
            this.memAppender = memAppender;
        }

        public void begin() {
            memAppender.clearAllCache();

        }

        public void begin(Level level) {
            memAppender.clearCache(level);
        }


        public void clear() {
            logger.detachAppender(memAppender);
            memAppender.clearAllCache();
        }

        public boolean match(Level logLevel, String regex) {
            return memAppender.match(logLevel, regex);
        }


    }

    static class MemAppender extends AppenderBase<ILoggingEvent> {
        Map<String, List<ILoggingEvent>> level2Event = new HashMap<>();

        @Override
        protected void append(ILoggingEvent iLoggingEvent) {
            String levelStr = iLoggingEvent.getLevel().levelStr;
            List<ILoggingEvent> iLoggingEvents = level2Event.computeIfAbsent(levelStr, key -> new ArrayList<>());
            iLoggingEvents.add(iLoggingEvent);
        }

        public void clearCache(Level level) {
            List<ILoggingEvent> iLoggingEvents = level2Event.get(level.toString());
            if (iLoggingEvents != null) {
                iLoggingEvents.clear();
            }
        }

        public void clearAllCache() {
            level2Event.forEach((k, v) -> v.clear());
        }

        public boolean match(Level logLevel, String regex) {
            List<ILoggingEvent> iLoggingEvents = level2Event.get(logLevel.toString());
            if (iLoggingEvents == null) {
                return false;
            }
            Pattern pattern = Pattern.compile(regex);
            return iLoggingEvents.stream().anyMatch(
                    event -> {
                        Matcher matcher = pattern.matcher(event.getMessage());
                        return matcher.matches();
                    }
            );
        }
    }

}
