package org.polaris2023.caft.quantum;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QuantumCooldownManager {
    private final Map<UUID, Long> cooldownEndTimes = new ConcurrentHashMap<>();

    public boolean canTrigger(Player player) {
        return !isOnCooldown(player, player.level().getGameTime());
    }

    public boolean isOnCooldown(Player player, long gameTime) {
        long cooldownEnd = cooldownEndTimes.getOrDefault(player.getUUID(), 0L);
        return gameTime < cooldownEnd;
    }

    public void startCooldown(Player player, int cooldownTicks) {
        long now = player.level().getGameTime();
        cooldownEndTimes.put(player.getUUID(), now + Math.max(0, cooldownTicks));
    }

    public long getRemainingTicks(Player player, long gameTime) {
        long cooldownEnd = cooldownEndTimes.getOrDefault(player.getUUID(), 0L);
        return Math.max(0L, cooldownEnd - gameTime);
    }

    public void clearCooldown(Player player) {
        cooldownEndTimes.remove(player.getUUID());
    }
}
