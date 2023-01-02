package com.uroria.kebab.network.protocol.minecraft.play.out;

import com.uroria.kebab.network.protocol.PacketOut;
import com.uroria.kebab.utils.DataTypeIO;
import com.uroria.kebab.utils.minecraft.GameMode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public final class PacketPlayOutPlayerInfo extends PacketOut {
    public static final byte PACKET_ID = 0x36;

    public enum PlayerInfoAction {
        ADD_PLAYER,
        INITIALIZE_CHAT,
        UPDATE_GAME_MODE,
        UPDATE_LISTED,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME;
    }

    private final EnumSet<PlayerInfoAction> actions;
    private final UUID uuid;
    private final PlayerInfoData data;

    public PacketPlayOutPlayerInfo(EnumSet<PlayerInfoAction> actions, UUID uuid, PlayerInfoData data) {
        this.actions = actions;
        this.uuid = uuid;
        this.data = data;
    }

    public EnumSet<PlayerInfoAction> getActions() {
        return actions;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerInfoData getData() {
        return data;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PACKET_ID);
        DataTypeIO.writeEnumSet(output, actions, PlayerInfoAction.class);
        DataTypeIO.writeVarInt(output, 1);
        DataTypeIO.writeUUID(output, uuid);
        PlayerInfoData.PlayerInfoDataAddPlayer data = (PlayerInfoData.PlayerInfoDataAddPlayer) this.data;
        for (PlayerInfoAction action : actions) {
            switch (action) {
                case ADD_PLAYER: {
                    DataTypeIO.writeString(output, data.getName(), StandardCharsets.UTF_8);
                    if (data.getSkin().isPresent()) {
                        DataTypeIO.writeVarInt(output, 1);
                        DataTypeIO.writeString(output, "textures", StandardCharsets.UTF_8);
                        DataTypeIO.writeString(output, data.getSkin().get().getSkin(), StandardCharsets.UTF_8);
                        output.writeBoolean(true);
                        DataTypeIO.writeString(output, data.getSkin().get().getSignature(), StandardCharsets.UTF_8);
                    } else DataTypeIO.writeVarInt(output, 0);
                    break;
                }
                case INITIALIZE_CHAT: {
                    break;
                }
                case UPDATE_GAME_MODE: {
                    DataTypeIO.writeVarInt(output, data.getGameMode().getId());
                    break;
                }
                case UPDATE_LISTED: {
                    output.writeBoolean(data.isListed());
                    break;
                }
                case UPDATE_LATENCY: {
                    DataTypeIO.writeVarInt(output, data.getPing());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    if (data.getDisplayNameJson().isPresent()) {
                        output.writeBoolean(true);
                        DataTypeIO.writeString(output, data.getDisplayNameJson().get(), StandardCharsets.UTF_8);
                    } else output.writeBoolean(false);
                    break;
                }
            }
        }
        return buffer.toByteArray();
    }

    public static class PlayerInfoData {
        public static class PlayerInfoDataAddPlayer extends PlayerInfoData {
            private final String name;
            private final boolean listed;
            private final Optional<PlayerSkinProperty> skin;
            private final GameMode gameMode;
            private final int ping;
            private final boolean hasDisplayName;
            private final Optional<String> displayNameJson;

            public PlayerInfoDataAddPlayer(String name, boolean listed, Optional<PlayerSkinProperty> skin, GameMode gameMode, int ping, boolean hasDisplayName, Optional<String> displayNameJson) {
                this.name = name;
                this.listed = listed;
                this.skin = skin;
                this.gameMode = gameMode;
                this.ping = ping;
                this.hasDisplayName = hasDisplayName;
                this.displayNameJson = displayNameJson;
            }

            public String getName() {
                return name;
            }

            public boolean isListed() {
                return listed;
            }

            public Optional<PlayerSkinProperty> getSkin() {
                return skin;
            }

            public GameMode getGameMode() {
                return gameMode;
            }

            public int getPing() {
                return ping;
            }

            public boolean isHasDisplayName() {
                return hasDisplayName;
            }

            public Optional<String> getDisplayNameJson() {
                return displayNameJson;
            }

            public static class PlayerSkinProperty {
                private final String skin;
                private final String signature;
                public PlayerSkinProperty(String skin, String signature) {
                    this.skin = skin;
                    this.signature = signature;
                }

                public String getSkin() {
                    return skin;
                }

                public String getSignature() {
                    return signature;
                }
            }
        }
    }
}
