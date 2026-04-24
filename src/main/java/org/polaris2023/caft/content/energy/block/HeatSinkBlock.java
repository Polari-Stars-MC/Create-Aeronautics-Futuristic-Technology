package org.polaris2023.caft.content.energy.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

public class HeatSinkBlock extends Block {
    public static final MapCodec<HeatSinkBlock> CODEC = simpleCodec(HeatSinkBlock::new);

    public HeatSinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
