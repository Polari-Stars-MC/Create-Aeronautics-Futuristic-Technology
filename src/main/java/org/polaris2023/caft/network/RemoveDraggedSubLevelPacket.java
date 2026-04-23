package org.polaris2023.caft.network;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        ServerLevelPlot plot = serverSubLevel.getPlot();
        if (plot.localBounds == null || plot.localBounds == BoundingBox3i.EMPTY) {
            return;
        }

        final Level level_ = plot.getSubLevel().getLevel();
        CreateAeronauticsFuturisticTechnology.LOGGER.error(level_.getDescriptionKey());
        final BoundingBox3i bounds = plot.localBounds;

        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    level_.destroyBlock(pos, false);
                }
            }
        }
        container.removeSubLevel(serverSubLevel, SubLevelRemovalReason.REMOVED);

    }
}
