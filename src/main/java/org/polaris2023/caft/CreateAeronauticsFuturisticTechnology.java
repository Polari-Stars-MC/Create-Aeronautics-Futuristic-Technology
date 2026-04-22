package org.polaris2023.caft;

import com.tterrag.registrate.Registrate;
import net.minecraft.resources.ResourceLocation;
import org.polaris2023.caft.network.FTPacketManager;
import org.polaris2023.caft.quantum.QuantumPropulsionPhaseOne;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModBlockEntities;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(CreateAeronauticsFuturisticTechnology.MODID)
public class CreateAeronauticsFuturisticTechnology {
    public static final String MODID = "caft";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MODID);

    public CreateAeronauticsFuturisticTechnology(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        QuantumPropulsionPhaseOne.bootstrap();
        ModBlockEntities.init();
        ModBlocks.init();
        FTPacketManager.init();
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
