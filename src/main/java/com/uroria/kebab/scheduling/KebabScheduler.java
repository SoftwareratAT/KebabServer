package com.uroria.kebab.scheduling;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public final class KebabScheduler {

    /**
     * Run a task now and once
     *
     * @param action Action to receive object
     * @return Asyncaction to run the task and receive callbacks
     * @param <T> The needed object
     */
    public <T> AsyncTask<T> runTask(Supplier<? extends T> action) {
        return new AsyncTask<>(action, this::backSync);
    }

    /**
     * Run a Task later
     *
     * @param action Action to receive object
     * @param time Time bound to TimeUnit
     * @param timeUnit TimeUnit bound to Time
     * @return AsyncAction to run the task and receive callbacks
     * @param <T> The needed object
     */
    public <T> AsyncTask<T> runTaskLater(Supplier<? extends T> action, long time, TimeUnit timeUnit) {
        return new AsyncTask<>(action, this::backSync, time, timeUnit);
    }

    /**
     * Run a Task timer
     *
     * @param action Action to receive object
     * @param time Time bound to TimeUnit
     * @param timeUnit The TimeUnit bound to Time
     * @return AsyncAction to run the task and receive callbacks
     * @param <T> The needed object
     */
    public <T> AsyncTask<T> runTaskTimer(Function<Integer, ? extends T> action, long time, TimeUnit timeUnit) {
        return new AsyncTask<>(action, this::backSync, time, timeUnit);
    }

    private void backSync(Runnable runnable) {
        runnable.run();
    }
}
