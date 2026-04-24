package org.polaris2023.caft.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.client.render.FutureEnergyCoreRenderer;
import org.polaris2023.caft.registry.ModBlockEntities;

@EventBusSubscriber(modid = CreateAeronauticsFuturisticTechnology.MODID, value = Dist.CLIENT)
public final class RenderEvents {
    private RenderEvents() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FUTURE_ENERGY_CORE.get(), FutureEnergyCoreRenderer::new);
    }
}
