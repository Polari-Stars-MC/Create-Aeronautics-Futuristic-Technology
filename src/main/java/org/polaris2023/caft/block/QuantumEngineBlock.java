package org.polaris2023.caft.block;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.quantum.EngineFeedback;
import org.polaris2023.caft.registry.ModBlockEntities;

public class QuantumEngineBlock extends BaseEntityBlock implements IBE<QuantumEngineBlockEntity> {
    public static final MapCodec<QuantumEngineBlock> CODEC = simpleCodec(QuantumEngineBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public QuantumEngineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Class<QuantumEngineBlockEntity> getBlockEntityClass() {
        return QuantumEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends QuantumEngineBlockEntity> getBlockEntityType() {
        return ModBlockEntities.QUANTUM_ENGINE.get();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof QuantumEngineBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            if (!isInSubLevel(blockEntity)) {
                blockEntity.activateQuantumJump(level, player);
                EngineFeedback feedback = blockEntity.getEngineFeedback();
                player.displayClientMessage(
                        Component.literal(feedback.message() + " Energy: " + Math.round(feedback.energyRatio() * 100.0F) + "%")
                                .withStyle(feedback.status().name().equals("FAULTED") ? ChatFormatting.RED : ChatFormatting.AQUA),
                        true
                );
            } else {
                SubLevel subLevel = (SubLevel) SableCompanion.INSTANCE.getContaining(blockEntity);
                blockEntity.activateQuantumJump(subLevel.getLevel(), player);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public boolean isInSubLevel(QuantumEngineBlockEntity be) {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(be);
        return subLevel != null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.QUANTUM_ENGINE.get(), QuantumEngineBlockEntity::serverTick);
    }
}
