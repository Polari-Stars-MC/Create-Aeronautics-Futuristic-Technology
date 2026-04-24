package org.polaris2023.caft;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.polaris2023.caft.network.FTPacketManager;
import org.polaris2023.caft.registry.ModBlockEntities;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModRecipes;
import org.slf4j.Logger;

@Mod(CreateAeronauticsFuturisticTechnology.MODID)
public class CreateAeronauticsFuturisticTechnology {
    public static final String MODID = "caft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateAeronauticsFuturisticTechnology(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        FTPacketManager.init();

    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
