package com.uroria.kebab.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ConsoleLogger implements Logger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss");

    private String prefix;

    private enum Level {
        INFO("[INFO]"),
        WARNING("[WARNING]"),
        ERROR("[ERROR]");

        private final String prefix;

        Level(String levelPrefix) {
            this.prefix =levelPrefix;
        }
    }

    private static String getTime() {
        return FORMATTER.format(LocalDateTime.now());
    }

    @Override
    public void info(String text) {
        System.out.println(getTime() + " " + Level.INFO.prefix + " " + text);

    }

    @Override
    public void warn(String text) {
        System.out.println(getTime() + " " + Level.WARNING.prefix + " " + text);
    }

    @Override
    public void error(String text) {
        error(text, null);
    }

    @Override
    public void error(String text, Throwable throwable) {
        System.out.println(getTime() + " " + Level.ERROR.prefix + " " + text);
        if (throwable != null) throwable.printStackTrace();
    }
}
