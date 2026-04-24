package org.polaris2023.caft.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.block.FutureEnergyCoreBlock;
import org.polaris2023.caft.block.EnergyConduitBlock;
import org.polaris2023.caft.block.HeatSinkBlock;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateAeronauticsFuturisticTechnology.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateAeronauticsFuturisticTechnology.MODID);

    public static final DeferredBlock<FutureEnergyCoreBlock> FUTURE_ENERGY_CORE = BLOCKS.registerBlock(
            "future_energy_core",
            FutureEnergyCoreBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F, 2.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(FutureEnergyCoreBlock.ACTIVE) ? Math.min(15, 12 + state.getValue(FutureEnergyCoreBlock.INTEGRITY) / 4) : 0)
    );
    public static final DeferredBlock<EnergyConduitBlock> ENERGY_CONDUIT = BLOCKS.registerBlock(
            "energy_conduit",
            EnergyConduitBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.5F, 1.5F)
                    .sound(SoundType.COPPER)
    );
    public static final DeferredBlock<HeatSinkBlock> HEAT_SINK = BLOCKS.registerBlock(
            "heat_sink",
            HeatSinkBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                    .strength(1.5F, 1.5F)
                    .sound(SoundType.NETHERITE_BLOCK)
    );


    private ModBlocks() {
    }

    private static void registerBlockItem(String name, DeferredBlock<? extends Block> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        {
            registerBlockItem("future_energy_core", FUTURE_ENERGY_CORE);
            registerBlockItem("energy_conduit", ENERGY_CONDUIT);
            registerBlockItem("heat_sink", HEAT_SINK);
        }
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
