package com.uroria.kebab.scheduling;

import com.uroria.kebab.KebabServer;
import com.uroria.kebab.logger.ConsoleLogger;
import jdk.internal.net.http.common.Pair;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class AsyncTask<T> {
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(0);
    private final ConsoleLogger logger = KebabServer.getInstance().getLogger();

    private final Function<Integer, ? extends T> function;
    private final Executor backSync;
    private final long time;
    private final TimeUnit timeUnit;

    AsyncTask(Supplier<? extends T> supplier, Executor backSync) {
        this(ignored -> supplier.get(), backSync, 0, TimeUnit.MILLISECONDS);
    }

    public AsyncTask(Supplier<? extends T> supplier, Executor backSync, long time, TimeUnit timeUnit) {
        this(ignored -> supplier.get(), backSync, time, timeUnit);
    }

    public AsyncTask(Function<Integer, ? extends T> function, Executor backSync, long time, TimeUnit timeUnit) {
        this.function = function;
        this.backSync = backSync;
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public void run(Consumer<? super T> success) {
        run(success, this::onError);
    }

    public void run(Consumer<? super T> success, Consumer<? super Throwable> error) {
        CompletableFuture<T> future = time == 0 ? submit() : submitLater(time, timeUnit);
        future.whenComplete((onSuccess, onError) -> {
            if (future.isCompletedExceptionally()) {
                error.accept(onError);
                return;
            }
            success.accept(onSuccess);
        });
    }

    public void run(BiConsumer<? super T, ? super ScheduledFuture<?>> success) {
        run(success, (throwable, runnable) -> throwable.printStackTrace());
    }

    public void run(BiConsumer<? super T, ? super ScheduledFuture<?>> success, BiConsumer<? super Throwable, ? super ScheduledFuture<?>> error) {
        submitTimer(success, error);
    }

    private CompletableFuture<T> submit() {
        CompletableFuture<T> future = new CompletableFuture<T>();
        processFuture(future, 1);
        return future;
    }

    private CompletableFuture<T> submitLater(long time, TimeUnit timeUnit) {
        CompletableFuture<T> future = new CompletableFuture<T>();
        EXECUTOR_SERVICE.schedule(() -> processFuture(future, 1), time, timeUnit);
        return future;
    }

    private void submitTimer(BiConsumer<? super T, ? super ScheduledFuture<?>> success, BiConsumer<? super Throwable, ? super ScheduledFuture<?>> error) {
        AtomicReference<ScheduledFuture<?>> scheduledFuture = new AtomicReference<ScheduledFuture<?>>(null);
        AtomicInteger counter = new AtomicInteger(1);
        scheduledFuture.set(EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            CompletableFuture<T> future = new CompletableFuture<T>();
            processFuture(future, counter.getAndIncrement());
            future.whenComplete((result, throwable) -> {
                if (future.isCompletedExceptionally()) {
                    error.accept(throwable, scheduledFuture.get());
                    return;
                }
                success.accept(result, scheduledFuture.get());
            });
        }, time, time, timeUnit));
    }

    private void processFuture(CompletableFuture<? super T> future, Integer counter) {
        Pair<T, Throwable> result = execute(counter).join();
        if (result.second != null) future.completeExceptionally(result.second);
        else future.complete(result.first);
    }


    private ForkJoinTask<Pair<T, Throwable>> execute(Integer counter) {
        return ForkJoinPool.commonPool().submit(ForkJoinTask.adapt(() -> {
            try {
                return new Pair<>(this.function.apply(counter), null);
            } catch (Throwable throwable) {
                return new Pair<>(null, throwable);
            }
        }));
    }

    private void onError(Throwable throwable) {
        this.logger.error("Unhandled Exception in AsyncAction", throwable);
    }

    public static final class AsyncExecutionException extends RuntimeException {
        private AsyncExecutionException(Throwable cause) {
            super(cause);
        }
    }
}
