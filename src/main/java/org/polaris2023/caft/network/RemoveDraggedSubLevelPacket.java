package org.polaris2023.caft.network;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;

import java.util.UUID;

public record RemoveDraggedSubLevelPacket(UUID subLevelId) implements CustomPacketPayload {
    public static final Type<RemoveDraggedSubLevelPacket> TYPE =
            new Type<>(CreateAeronauticsFuturisticTechnology.path("remove_dragged_sub_level"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveDraggedSubLevelPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    RemoveDraggedSubLevelPacket::subLevelId,
                    RemoveDraggedSubLevelPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RemoveDraggedSubLevelPacket packet, ServerPacketContext context) {
        ServerLevel level = context.player().serverLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }

        if (!(container.getSubLevel(packet.subLevelId()) instanceof ServerSubLevel serverSubLevel)) {
            return;
        }

        container.removeSubLevel(serverSubLevel, SubLevelRemovalReason.REMOVED);
    }
}
