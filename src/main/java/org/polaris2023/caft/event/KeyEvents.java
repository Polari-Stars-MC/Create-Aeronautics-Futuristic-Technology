package org.polaris2023.caft.event;

import com.mojang.blaze3d.platform.InputConstants;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler;
import dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffDragPacket;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.network.RemoveDraggedSubLevelPacket;

@EventBusSubscriber(modid = CreateAeronauticsFuturisticTechnology.MODID, value = Dist.CLIENT)
public class KeyEvents {

    public static final KeyMapping DELETE = new KeyMapping(
            "key.caft.del",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_DELETE,
            "key.categories.caft"
    );

    public static final KeyMapping HOME = new KeyMapping(
            "key.caft.adjust",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.caft"
    );

    @SubscribeEvent
    public static void bind(RegisterKeyMappingsEvent event) {
        event.register(DELETE);
        event.register(HOME);
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        PhysicsStaffClientHandler handler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
        PhysicsStaffClientHandler.ClientDragSession session = handler.getDragSession();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!player.isCreative()) {
            return;
        }
        while (DELETE.consumeClick() && session != null) {


            VeilPacketManager.server().sendPacket(new RemoveDraggedSubLevelPacket(session.dragSubLevel().getUniqueId()));
            handler.stopDragging();
        }
        while (HOME.consumeClick() && session != null) {
            session.dragOrientation().identity();
            Vec3 playerRelativeGoal = player.getLookAngle().scale(session.distance());
            VeilPacketManager.server().sendPacket(new PhysicsStaffDragPacket(
                    session.dragSubLevel().getUniqueId(),
                    JOMLConversion.toJOML(playerRelativeGoal),
                    session.dragLocalAnchor(),
                    session.dragOrientation()
            ));
        }
    }

}
