package org.polaris2023.caft.quantum;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IQuantumPropulsion {
    void activateQuantumJump(Level level, Player player);

    void updateEngineStatus();

    EngineFeedback getEngineFeedback();

    boolean isCoolingDown();

    float getCurrentEnergyRatio();
}
