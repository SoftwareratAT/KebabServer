package com.uroria.kebab.scheduling;

import com.uroria.kebab.KebabServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class Tick {
    private int tickingInterval;
    private final AtomicLong tick = new AtomicLong(0);

    public Tick(KebabServer instance) {
        new Thread(() -> {
            tickingInterval = (int) Math.round(1000.0 / Limbo.getInstance().getServerProperties().getDefinedTicksPerSecond());

            while (instance.isRunning()) {
                long start = System.currentTimeMillis();
                tick.incrementAndGet();
                instance.getPlayers().forEach(each -> {
                    if (each.clientConnection.isReady()) {
                        try {
                            each.playerInteractManager.update();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                instance.getWorlds().forEach(each -> {
                    try {
                        each.update();
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

                long end = System.currentTimeMillis();
                try {
                    TimeUnit.MILLISECONDS.sleep(tickingInterval - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public long getCurrentTick() {
        return tick.get();
    }
}
