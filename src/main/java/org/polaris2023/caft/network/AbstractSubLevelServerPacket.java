package org.polaris2023.caft.network;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface AbstractSubLevelServerPacket extends CustomPacketPayload {
    static ServerSubLevelContainer handle(AbstractSubLevelServerPacket packet, ServerPacketContext context) {
        ServerPlayer player = context.player();
        if (!player.isCreative()) return null;//防止被恶意发包
        ServerLevel level = player.serverLevel();
        return SubLevelContainer.getContainer(level);
    }
}
