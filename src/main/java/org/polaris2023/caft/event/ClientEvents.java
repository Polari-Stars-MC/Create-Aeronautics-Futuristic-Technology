package org.polaris2023.caft.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
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
}
