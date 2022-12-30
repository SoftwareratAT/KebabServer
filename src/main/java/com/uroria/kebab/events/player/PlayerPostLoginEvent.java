package com.uroria.kebab.events.player;

import com.uroria.kebab.events.Cancellable;
import com.uroria.kebab.events.Event;
import com.uroria.kebab.location.Location;
import com.uroria.kebab.player.Player;
import com.uroria.kebab.player.KebabPlayer;
import com.uroria.kebab.utils.minecraft.GameMode;
import com.uroria.kebab.world.World;
import net.kyori.adventure.text.Component;

public class PlayerPostLoginEvent extends Event implements Cancellable {
    private final Player player;
    private GameMode gameMode;
    private World spawnWorld;
    private Location spawnLocation;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean hardcore;
    private Component serverBrand;
    private boolean cancelled;
    private Component reason;

    public PlayerPostLoginEvent(KebabPlayer player, GameMode gameMode, World spawnWorld, Location spawnLocation, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean enableRespawnScreen, boolean hardcore, Component serverBrand, boolean cancelled, Component reason) {
        this.player = player;
        this.gameMode = gameMode;
        this.spawnWorld = spawnWorld;
        this.spawnLocation = spawnLocation;
        this.viewDistance = viewDistance;
        this.simulationDistance = simulationDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.enableRespawnScreen = enableRespawnScreen;
        this.hardcore = hardcore;
        this.serverBrand = serverBrand;
        this.cancelled = cancelled;
        this.reason = reason;
    }

    public Player getPlayer() {
        return player;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public World getSpawnWorld() {
        return spawnWorld;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getSimulationDistance() {
        return simulationDistance;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }

    public boolean isEnableRespawnScreen() {
        return enableRespawnScreen;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public Component getReason() {
        return reason;
    }

    public Component getServerBrand() {
        return serverBrand;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setSpawnWorld(World spawnWorld) {
        this.spawnWorld = spawnWorld;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    public void setSimulationDistance(int simulationDistance) {
        this.simulationDistance = simulationDistance;
    }

    public void enableReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public void enableRespawnScreen(boolean enableRespawnScreen) {
        this.enableRespawnScreen = enableRespawnScreen;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public void setReason(Component reason) {
        this.reason = reason;
    }

    public void setServerBrand(Component serverBrand) {
        this.serverBrand = serverBrand;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
