package org.polaris2023.caft.quantum;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.polaris2023.caft.Config;

public final class QuantumFailureLogic {
    private static final double BASE_FAILURE_CHANCE = 0.02D;
    private static final double LOW_ENERGY_BONUS = 0.12D;
    private static final double DAMAGE_LEVEL_STEP = 0.04D;
    private static final double MAX_DURABILITY_PENALTY = 0.10D;

    private QuantumFailureLogic() {
    }

    public static boolean shouldFail(QuantumEngineState state) {
        return shouldFail(state, RandomSource.create());
    }

    public static boolean shouldFail(QuantumEngineState state, RandomSource random) {
        double failureChance = BASE_FAILURE_CHANCE;
        if (state.isLowEnergy(Config.LOW_ENERGY_FAILURE_THRESHOLD.get())) {
            failureChance += LOW_ENERGY_BONUS;
        }

        failureChance += state.damageLevel() * DAMAGE_LEVEL_STEP;
        failureChance += (1.0D - state.durabilityRatio()) * MAX_DURABILITY_PENALTY;
        failureChance += Math.min(0.08D, state.overheatTicks() / 200.0D);
        failureChance = Math.min(failureChance, Config.MAX_FAILURE_CHANCE.get());

        return random.nextDouble() < failureChance;
    }

    public static Vector3d randomOffset(RandomSource random) {
        double distance = 2.0D + random.nextDouble() * 3.0D;
        double angle = random.nextDouble() * (Math.PI * 2.0D);
        double vertical = -0.5D + random.nextDouble();
        return new Vector3d(
                Math.cos(angle) * distance,
                vertical,
                Math.sin(angle) * distance
        );
    }

    public static void applyFailureEffects(Player player, QuantumEngineState state) {
        RandomSource random = player.getRandom();
        Vec3 currentVelocity = player.getDeltaMovement();
        boolean reverseVelocity = random.nextBoolean();
        Vec3 adjustedVelocity = reverseVelocity ? currentVelocity.scale(-0.6D) : Vec3.ZERO;
        player.setDeltaMovement(adjustedVelocity);
        player.hurtMarked = true;

        if (Config.ENABLE_BACKLASH_DAMAGE.get()) {
            DamageSource damageSource = player.damageSources().magic();
            float damage = 2.0F + (float) state.damageLevel();
            player.hurt(damageSource, damage);
        }
    }
}
