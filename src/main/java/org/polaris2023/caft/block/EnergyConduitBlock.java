package org.polaris2023.caft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public class EnergyConduitBlock extends Block {
    public static final MapCodec<EnergyConduitBlock> CODEC = simpleCodec(EnergyConduitBlock::new);

    public EnergyConduitBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
