package com.uroria.kebab.scheduling;

import com.uroria.kebab.KebabServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class Tick {
    private int tickingInterval;
    private final AtomicLong tick = new AtomicLong(0);

    public Tick(KebabServer instance, int tps) {
        new Thread(() -> {
            tickingInterval = (int) Math.round(1000.0 / tps);
            while (instance.isRunning()) {
                long start = System.currentTimeMillis();
                tick.incrementAndGet();
                instance.getPlayers().forEach(player -> {
                    if (player.getClientConnection().isReady()) {
                        try {
                            player.getPlayerInteractManager().update();
                        } catch (IOException exception) {
                            KebabServer.getInstance().getLogger().error("Error while trying to update player " + player.getName() + " on Tick " + getCurrentTick(), exception);
                        }
                    }
                });
                instance.getWorlds().forEach(world -> {
                    try {
                        world.update();
                    } catch (IllegalArgumentException | IllegalAccessException exception) {
                        KebabServer.getInstance().getLogger().error("Error while trying to update world " + world.getName() + " on Tick " + getCurrentTick(), exception);
                    }
                });
                long end = System.currentTimeMillis();
                try {
                    TimeUnit.MILLISECONDS.sleep(tickingInterval - (end - start));
                } catch (InterruptedException exception) {
                    KebabServer.getInstance().getLogger().error("Error while trying to let Thread sleep on Tick " + getCurrentTick(), exception);
                }
            }
        }).start();
    }

    public long getCurrentTick() {
        return tick.get();
    }
}
