package org.polaris2023.caft.quantum;

import net.minecraft.world.entity.player.Player;

public class MountedQuantumPropulsionController extends AbstractQuantumPropulsionController {
    private final QuantumMountType mountType;
    private double jumpDistance;
    private Player feedbackPlayer;

    public MountedQuantumPropulsionController(
            QuantumMountType mountType,
            double jumpDistance,
            QuantumEngineData engineData,
            QuantumEngineState engineState) {
        super(engineData, engineState, QuantumUpgradeProfile.forMount(mountType));
        this.mountType = mountType;
        this.jumpDistance = jumpDistance;
    }

    public void bindPlayer(Player player) {
        this.feedbackPlayer = player;
    }

    public void setJumpDistance(double jumpDistance) {
        this.jumpDistance = jumpDistance;
    }

    @Override
    public QuantumMountType getQuantumMountType() {
        return mountType;
    }

    @Override
    protected double getJumpDistance() {
        return jumpDistance;
    }

    @Override
    protected Player getFeedbackPlayer() {
        return feedbackPlayer;
    }
}
