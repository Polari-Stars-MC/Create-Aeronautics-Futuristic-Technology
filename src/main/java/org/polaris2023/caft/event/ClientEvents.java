package org.polaris2023.caft.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.compat.CompatMods;

@EventBusSubscriber(modid = CreateAeronauticsFuturisticTechnology.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void client(FMLClientSetupEvent event) {
        if (CompatMods.PONDER.isLoader()) {
            CompatMods.PONDER.run();
        }
    }

    @SubscribeEvent
    public static void recipesUpdated(RecipesUpdatedEvent event) {
        if (CompatMods.JEI.isLoader()) {
            CompatMods.JEI.listening(0, event);
        }
    }

    @SubscribeEvent
    public static void mouseDragged(ScreenEvent.MouseDragged.Pre event) {
        if (CompatMods.EMI.isLoader()) {
            CompatMods.EMI.listening(0, event);
        }
    }

    @SubscribeEvent
    public static void mouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (CompatMods.EMI.isLoader()) {
            CompatMods.EMI.listening(1, event);
        }
    }

    @SubscribeEvent
    public static void mouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (CompatMods.EMI.isLoader()) {
            CompatMods.EMI.listening(2, event);
        }
    }
}
