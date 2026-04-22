package org.polaris2023.caft.network;

import foundry.veil.api.network.VeilPacketManager;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;

public class FTPacketManager {
    public static final VeilPacketManager INSTANCE = VeilPacketManager.create(CreateAeronauticsFuturisticTechnology.MODID, "1");

    public static void init() {
        INSTANCE.registerServerbound(RemoveDraggedSubLevelPacket.TYPE, RemoveDraggedSubLevelPacket.CODEC, RemoveDraggedSubLevelPacket::handle);
    }
}
