package org.polaris2023.caft.registry;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.polaris2023.caft.block.QuantumEngineBlock;

import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.REGISTRATE;

public class ModBlocks {
    public static final BlockEntry<QuantumEngineBlock> QUANTUM_ENGINE = REGISTRATE
            .block("quantum_engine", QuantumEngineBlock::new)
            .properties(properties -> properties
                    .strength(4.0F, 6.0F)
                    .sound(SoundType.NETHERITE_BLOCK))
            .defaultLang()
            .blockstate((ctx, prov) -> {
                ResourceLocation id = ctx.getId();
                ResourceLocation blockId = id.withPrefix("block/");
                prov.horizontalBlock(ctx.get(), prov.models().orientableWithBottom(id.getPath(), blockId.withSuffix("_side"), blockId.withSuffix("_front"), blockId.withSuffix("_bottom"), blockId.withSuffix("_top")));
            })
            .simpleItem()
            .register();

    public static void init() {}
}
