package org.polaris2023.caft.quantum;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.polaris2023.caft.Config;

public abstract class AbstractQuantumPropulsionController implements IQuantumPropulsion, IQuantumUpgradeCarrier {
    private final QuantumEngineData engineData;
    private final QuantumCooldownManager cooldownManager;
    private final QuantumUpgradeProfile upgradeProfile;
    private QuantumEngineState engineState;
    private EngineFeedback engineFeedback;

    protected AbstractQuantumPropulsionController(
            QuantumEngineData engineData,
            QuantumEngineState engineState,
            QuantumUpgradeProfile upgradeProfile) {
        this.engineData = engineData;
        this.engineState = engineState;
        this.upgradeProfile = upgradeProfile == null ? QuantumUpgradeProfile.BASE : upgradeProfile;
        this.cooldownManager = new QuantumCooldownManager();
        this.engineFeedback = EngineFeedback.ready(
                getCurrentEnergyRatio(),
                this.upgradeProfile.clampPrecision(Config.PRECISION_ERROR_MIN.get())
        );
    }

    @Override
    public QuantumUpgradeProfile getQuantumUpgradeProfile() {
        return upgradeProfile;
    }

    @Override
    public void activateQuantumJump(Level level, Player player) {
        updateEngineStatus();
        if (isCoolingDown()) {
            return;
        }

        QuantumUpgradeProfile upgradeProfile = getQuantumUpgradeProfile();
        int energyCost = upgradeProfile.applyEnergyCost(engineData.getEnergyConsumptionPerJump());
        if (!engineData.consumeEnergy(energyCost)) {
            engineFeedback = EngineFeedback.insufficientEnergy(getCurrentEnergyRatio(), estimateFailureChance());
            return;
        }

        if (QuantumFailureLogic.shouldFail(engineState)) {
            Vector3d offset = QuantumFailureLogic.randomOffset(player.getRandom());
            Vec3 safeFailureTarget = QuantumJumpCalculator.sanitizeTarget(
                    level,
                    player.getBoundingBox(),
                    player.position(),
                    new Vec3(player.getX() + offset.x, player.getY() + offset.y, player.getZ() + offset.z)
            );
            player.teleportTo(safeFailureTarget.x, safeFailureTarget.y, safeFailureTarget.z);
            QuantumFailureLogic.applyFailureEffects(player, engineState);
            cooldownManager.startCooldown(player, getEffectiveCooldownTicks());
            engineFeedback = EngineFeedback.faulted(getCurrentEnergyRatio(), getEffectiveCooldownTicks(), estimateFailureChance());
            return;
        }

        Vec3 previousVelocity = player.getDeltaMovement();
        Vector3d target = QuantumJumpCalculator.computeJumpPosition(player, getJumpDistance(), getEffectivePrecisionError());
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.teleportTo(target.x, target.y, target.z);
        } else {
            player.teleportTo(target.x, target.y, target.z);
        }

        QuantumJumpCalculator.applyConfiguredMomentum(player, previousVelocity);
        cooldownManager.startCooldown(player, getEffectiveCooldownTicks());
        engineFeedback = EngineFeedback.cooling(getCurrentEnergyRatio(), getEffectiveCooldownTicks());
    }

    @Override
    public void updateEngineStatus() {
        Player owner = getFeedbackPlayer();
        if (owner != null) {
            int remainingCooldownTicks = (int) cooldownManager.getRemainingTicks(owner, owner.level().getGameTime());
            if (remainingCooldownTicks > 0) {
                engineFeedback = EngineFeedback.cooling(getCurrentEnergyRatio(), remainingCooldownTicks);
                return;
            }
        }

        double failureChance = estimateFailureChance();
        if (!engineData.canJump()) {
            engineFeedback = EngineFeedback.insufficientEnergy(getCurrentEnergyRatio(), failureChance);
            return;
        }

        engineFeedback = EngineFeedback.ready(getCurrentEnergyRatio(), getEffectivePrecisionError());
    }

    @Override
    public EngineFeedback getEngineFeedback() {
        return engineFeedback;
    }

    @Override
    public boolean isCoolingDown() {
        Player owner = getFeedbackPlayer();
        return owner != null && cooldownManager.isOnCooldown(owner, owner.level().getGameTime());
    }

    @Override
    public float getCurrentEnergyRatio() {
        return (float) engineData.getEnergyRatio();
    }

    public QuantumEngineData getEngineData() {
        return engineData;
    }

    public QuantumEngineState getEngineState() {
        return engineState;
    }

    public void setEngineState(QuantumEngineState engineState) {
        this.engineState = engineState;
    }

    protected int getEffectiveCooldownTicks() {
        return upgradeProfile.applyCooldownReduction(Config.DEFAULT_COOLDOWN_TICKS.get());
    }

    protected double getEffectivePrecisionError() {
        return upgradeProfile.clampPrecision(Config.PRECISION_ERROR_MIN.get());
    }

    protected double estimateFailureChance() {
        double chance = 0.02D;
        if (engineState.isLowEnergy(Config.LOW_ENERGY_FAILURE_THRESHOLD.get())) {
            chance += 0.12D;
        }
        chance += engineState.damageLevel() * 0.04D;
        chance += (1.0D - engineState.durabilityRatio()) * 0.10D;
        chance += Math.min(0.08D, engineState.overheatTicks() / 200.0D);
        return Math.min(chance, Config.MAX_FAILURE_CHANCE.get());
    }

    protected int getRemainingCooldownTicks(Player player) {
        return (int) cooldownManager.getRemainingTicks(player, player.level().getGameTime());
    }

    protected abstract double getJumpDistance();

    protected abstract Player getFeedbackPlayer();
}
