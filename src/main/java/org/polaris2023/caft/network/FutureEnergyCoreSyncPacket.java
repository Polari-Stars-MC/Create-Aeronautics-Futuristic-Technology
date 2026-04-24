package org.polaris2023.caft.network;

import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.blockentity.FutureEnergyCoreBlockEntity;

public record FutureEnergyCoreSyncPacket(BlockPos pos, int energy, int heat, int integrity, int requiredIntegrity, boolean active) implements CustomPacketPayload {
    public static final Type<FutureEnergyCoreSyncPacket> TYPE =
            new Type<>(CreateAeronauticsFuturisticTechnology.path("future_energy_core_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FutureEnergyCoreSyncPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    FutureEnergyCoreSyncPacket::pos,
                    ByteBufCodecs.INT,
                    FutureEnergyCoreSyncPacket::energy,
                    ByteBufCodecs.INT,
                    FutureEnergyCoreSyncPacket::heat,
                    ByteBufCodecs.INT,
                    FutureEnergyCoreSyncPacket::integrity,
                    ByteBufCodecs.INT,
                    FutureEnergyCoreSyncPacket::requiredIntegrity,
                    ByteBufCodecs.BOOL,
                    FutureEnergyCoreSyncPacket::active,
                    FutureEnergyCoreSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FutureEnergyCoreSyncPacket packet, ClientPacketContext context) {
        if (context.level() == null) {
            return;
        }

        BlockEntity blockEntity = context.level().getBlockEntity(packet.pos());
        if (blockEntity instanceof FutureEnergyCoreBlockEntity energyCore) {
            energyCore.applyClientSync(packet.energy(), packet.heat(), packet.integrity(), packet.requiredIntegrity(), packet.active());
        }
    }
}
