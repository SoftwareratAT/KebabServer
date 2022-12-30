package com.uroria.kebab.logger;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public final class ConsoleLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogger.class);

    public void info(String text) {
        LOGGER.debug(text);
    }

    public void warn(String text) {
        LOGGER.warn(text);
    }

    public void error(String text) {
        LOGGER.error(text);
    }

    public void error(String text, Throwable throwable) {
        LOGGER.error(text, throwable);
    }
}
