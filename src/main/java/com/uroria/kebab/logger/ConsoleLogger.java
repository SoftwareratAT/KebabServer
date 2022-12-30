package com.uroria.kebab.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ConsoleLogger implements Logger {
    private enum Level {
        INFO("[INFO]"),
        WARNING("[WARNING]"),
        ERROR("[ERROR]");

        private final String prefix;

        Level(String levelPrefix) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            String time = formatter.format(currentTime);
            this.prefix = time + " " + levelPrefix;
        }
    }

    @Override
    public void info(String text) {
        System.out.println(Level.INFO.prefix + " " + text);

    }

    @Override
    public void warn(String text) {
        System.out.println(Level.WARNING.prefix + " " + text);
    }

    @Override
    public void error(String text) {
        error(text, null);
    }

    @Override
    public void error(String text, Throwable throwable) {
        System.out.println(Level.ERROR.prefix + " " + text);
        if (throwable != null) throwable.printStackTrace();
    }
}
