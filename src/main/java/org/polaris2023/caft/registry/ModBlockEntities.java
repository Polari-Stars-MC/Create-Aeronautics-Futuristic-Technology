package org.polaris2023.caft.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.blockentity.FutureEnergyCoreBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateAeronauticsFuturisticTechnology.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FutureEnergyCoreBlockEntity>> FUTURE_ENERGY_CORE =
            BLOCK_ENTITY_TYPES.register("future_energy_core",
                    () -> BlockEntityType.Builder.of(FutureEnergyCoreBlockEntity::new, ModBlocks.FUTURE_ENERGY_CORE.get()).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
