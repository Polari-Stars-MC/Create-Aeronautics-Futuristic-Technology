package org.polaris2023.caft.block;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.polaris2023.caft.registry.ModBlockEntities;

public class PCABlock extends DirectionalKineticBlock implements IBE<PCABlockEntity> {
    public PCABlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }


    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, net.minecraft.core.BlockPos pos, BlockState state, Direction face) {
        return state.getValue(FACING) == face;
    }

    @Override
    public Class<PCABlockEntity> getBlockEntityClass() {
        return PCABlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PCABlockEntity> getBlockEntityType() {
        return ModBlockEntities.PCA.get();
    }
}
