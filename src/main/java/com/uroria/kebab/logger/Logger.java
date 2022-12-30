package com.uroria.kebab.logger;

public interface Logger {
    void info(String text);

    void warn(String text);

    void error(String text);

    void error(String text, Throwable error);
}
