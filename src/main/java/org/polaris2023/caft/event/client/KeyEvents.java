package org.polaris2023.caft.event.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.network.RemoveDraggedSubLevelPacket;

@EventBusSubscriber(modid = CreateAeronauticsFuturisticTechnology.MODID)
public class KeyEvents {

    public static final KeyMapping DELETE = new KeyMapping(
            "key.caft.del",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_DELETE,
            "key.categories.caft.del"
    );

    @SubscribeEvent
    public static void bind(RegisterKeyMappingsEvent event) {
        event.register(DELETE);
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        while (DELETE.consumeClick()) {
            PhysicsStaffClientHandler handler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
            PhysicsStaffClientHandler.ClientDragSession session = handler.getDragSession();
            if (session == null) return;

            VeilPacketManager.server().sendPacket(new RemoveDraggedSubLevelPacket(session.dragSubLevel().getUniqueId()));
            handler.stopDragging();
        }
    }

}
