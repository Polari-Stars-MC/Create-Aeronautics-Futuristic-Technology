package org.polaris2023.caft.network;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;

import java.util.UUID;

public record RealignDraggedSubLevelPacket(UUID subLevelId) implements CustomPacketPayload {
    public static final Type<RealignDraggedSubLevelPacket> TYPE =
            new Type<>(CreateAeronauticsFuturisticTechnology.path("realign_dragged_sub_level"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RealignDraggedSubLevelPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    RealignDraggedSubLevelPacket::subLevelId,
                    RealignDraggedSubLevelPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RealignDraggedSubLevelPacket packet, ServerPacketContext context) {
        ServerLevel level = context.player().serverLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }

        if (!(container.getSubLevel(packet.subLevelId()) instanceof ServerSubLevel serverSubLevel)) {
            return;
        }

        Vector3d currentPosition = new Vector3d(serverSubLevel.logicalPose().position());
        Vector3d alignedPosition = new Vector3d(
                Math.rint(currentPosition.x),
                Math.rint(currentPosition.y),
                Math.rint(currentPosition.z)
        );
        Quaterniond alignedOrientation = new Quaterniond();

        RigidBodyHandle.of(serverSubLevel).teleport(alignedPosition, alignedOrientation);
        serverSubLevel.logicalPose().position().set(alignedPosition);
        serverSubLevel.logicalPose().orientation().set(alignedOrientation);
        serverSubLevel.latestLinearVelocity.zero();
        serverSubLevel.latestAngularVelocity.zero();
        serverSubLevel.updateBoundingBox();
    }
}
