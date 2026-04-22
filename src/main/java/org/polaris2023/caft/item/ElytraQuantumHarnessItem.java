package org.polaris2023.caft.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.polaris2023.caft.Config;
import org.polaris2023.caft.quantum.EngineFeedback;
import org.polaris2023.caft.quantum.IQuantumPropulsion;
import org.polaris2023.caft.quantum.IQuantumUpgradeCarrier;
import org.polaris2023.caft.quantum.MountedQuantumPropulsionController;
import org.polaris2023.caft.quantum.QuantumEngineData;
import org.polaris2023.caft.quantum.QuantumEngineState;
import org.polaris2023.caft.quantum.QuantumMountType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ElytraQuantumHarnessItem extends Item implements IQuantumPropulsion, IQuantumUpgradeCarrier {
    private final Map<UUID, MountedQuantumPropulsionController> controllersByPlayer = new ConcurrentHashMap<>();
    private UUID lastUserId;

    public ElytraQuantumHarnessItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        MountedQuantumPropulsionController controller = getOrCreateController(player);
        controller.bindPlayer(player);
        if (!level.isClientSide) {
            controller.activateQuantumJump(level, player);
        }
        lastUserId = player.getUUID();
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }

    @Override
    public QuantumMountType getQuantumMountType() {
        return QuantumMountType.ELYTRA_HARNESS;
    }

    @Override
    public void activateQuantumJump(Level level, Player player) {
        MountedQuantumPropulsionController controller = getOrCreateController(player);
        controller.bindPlayer(player);
        controller.activateQuantumJump(level, player);
        lastUserId = player.getUUID();
    }

    @Override
    public void updateEngineStatus() {
        MountedQuantumPropulsionController controller = getLastController();
        if (controller != null) {
            controller.updateEngineStatus();
        }
    }

    @Override
    public EngineFeedback getEngineFeedback() {
        MountedQuantumPropulsionController controller = getLastController();
        return controller == null ? EngineFeedback.insufficientEnergy(0.0F, 0.0D) : controller.getEngineFeedback();
    }

    @Override
    public boolean isCoolingDown() {
        MountedQuantumPropulsionController controller = getLastController();
        return controller != null && controller.isCoolingDown();
    }

    @Override
    public float getCurrentEnergyRatio() {
        MountedQuantumPropulsionController controller = getLastController();
        return controller == null ? 0.0F : controller.getCurrentEnergyRatio();
    }

    private MountedQuantumPropulsionController getOrCreateController(Player player) {
        return controllersByPlayer.computeIfAbsent(player.getUUID(), uuid -> {
            QuantumEngineData engineData = new QuantumEngineData(8000, Config.BLINK_BASE_ENERGY_COST.get());
            QuantumEngineState engineState = new QuantumEngineState(engineData, 1.0D, 0, 0);
            MountedQuantumPropulsionController controller = new MountedQuantumPropulsionController(
                    QuantumMountType.ELYTRA_HARNESS,
                    Config.BLINK_MAX_DISTANCE.get() + 4.0D,
                    engineData,
                    engineState
            );
            controller.bindPlayer(player);
            return controller;
        });
    }

    private MountedQuantumPropulsionController getLastController() {
        return lastUserId == null ? null : controllersByPlayer.get(lastUserId);
    }
}
