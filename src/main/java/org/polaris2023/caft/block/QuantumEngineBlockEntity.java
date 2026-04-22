package org.polaris2023.caft.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.polaris2023.caft.Config;
import org.polaris2023.caft.quantum.EngineFeedback;
import org.polaris2023.caft.quantum.IQuantumPropulsion;
import org.polaris2023.caft.quantum.IQuantumUpgradeCarrier;
import org.polaris2023.caft.quantum.MountedQuantumPropulsionController;
import org.polaris2023.caft.quantum.QuantumEngineData;
import org.polaris2023.caft.quantum.QuantumEngineState;
import org.polaris2023.caft.quantum.QuantumMountType;
import org.polaris2023.caft.registry.ModBlockEntities;

public class QuantumEngineBlockEntity extends BlockEntity implements IQuantumPropulsion, IQuantumUpgradeCarrier {
    private final MountedQuantumPropulsionController propulsionController;

    public QuantumEngineBlockEntity(BlockPos pos, BlockState blockState) {
        this(ModBlockEntities.QUANTUM_ENGINE.get(), pos, blockState);
    }

    public QuantumEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        QuantumEngineData engineData = new QuantumEngineData(12000, Config.BLINK_BASE_ENERGY_COST.get());
        this.propulsionController = new MountedQuantumPropulsionController(
                QuantumMountType.CREATE_SHAFT,
                Config.BLINK_MAX_DISTANCE.get(),
                engineData,
                new QuantumEngineState(engineData, 1.0D, 0, 0)
        );
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumEngineBlockEntity blockEntity) {
        blockEntity.propulsionController.getEngineData().rechargeOverTime(20);
        blockEntity.updateEngineStatus();
        blockEntity.setChanged();
    }

    @Override
    public QuantumMountType getQuantumMountType() {
        return QuantumMountType.CREATE_SHAFT;
    }

    @Override
    public void activateQuantumJump(Level level, Player player) {
        propulsionController.bindPlayer(player);
        propulsionController.activateQuantumJump(level, player);
        updateEngineStatus();
    }

    @Override
    public void updateEngineStatus() {
        propulsionController.updateEngineStatus();
    }

    @Override
    public EngineFeedback getEngineFeedback() {
        return propulsionController.getEngineFeedback();
    }

    @Override
    public boolean isCoolingDown() {
        return propulsionController.isCoolingDown();
    }

    @Override
    public float getCurrentEnergyRatio() {
        return propulsionController.getCurrentEnergyRatio();
    }
}
