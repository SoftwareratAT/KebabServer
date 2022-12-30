package com.uroria.kebab.logger;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public final class ConsoleLogger implements com.uroria.kebab.logger.Logger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogger.class);

    @Override
    public void info(String text) {
        LOGGER.debug(text);
    }

    @Override
    public void warn(String text) {
        LOGGER.warn(text);
    }

    @Override
    public void error(String text) {
        LOGGER.error(text);
    }

    @Override
    public void error(String text, Throwable throwable) {
        LOGGER.error(text, throwable);
    }
}
