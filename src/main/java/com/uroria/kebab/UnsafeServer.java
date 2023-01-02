package com.uroria.kebab;

import com.uroria.kebab.player.KebabPlayer;

public final class UnsafeServer {
    private final KebabServer server;
    public UnsafeServer(KebabServer instance) {
        this.server = instance;
    }

    public void addPlayer(KebabPlayer player) {
        this.server.players.put(player.getUniqueId(), player);
    }
}
