package org.polaris2023.caft;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue ENERGY_CAPACITY = BUILDER
            .comment("Maximum Aeronautical Force stored by the core")
            .defineInRange("energyCore.capacity", 10000, 1000, 1000000);

    public static final ModConfigSpec.DoubleValue BASE_EFFICIENCY = BUILDER
            .comment("Minimum conversion efficiency for an incomplete structure")
            .defineInRange("energyCore.baseEfficiency", 0.8D, 0.1D, 1.0D);

    public static final ModConfigSpec.DoubleValue COMPLETE_EFFICIENCY = BUILDER
            .comment("Maximum conversion efficiency for a complete structure")
            .defineInRange("energyCore.completeEfficiency", 1.0D, 0.1D, 2.0D);

    public static final ModConfigSpec.IntValue OVERHEAT_THRESHOLD_TICKS = BUILDER
            .comment("Continuous active time before heat penalties begin")
            .defineInRange("energyCore.overheatThresholdTicks", 20 * 30, 20, 20 * 60 * 10);

    public static final ModConfigSpec.DoubleValue OVERHEAT_MAX_PENALTY = BUILDER
            .comment("Maximum efficiency penalty caused by overheating")
            .defineInRange("energyCore.overheatMaxPenalty", 0.35D, 0.0D, 0.95D);

    public static final ModConfigSpec.IntValue PASSIVE_COOLDOWN_PER_TICK = BUILDER
            .comment("Cooling restored per tick while the structure is stable")
            .defineInRange("energyCore.passiveCoolingPerTick", 2, 0, 100);

    public static final ModConfigSpec.IntValue HEAT_GAIN_PER_TICK = BUILDER
            .comment("Heat added per active conversion tick")
            .defineInRange("energyCore.heatGainPerTick", 4, 1, 100);

    public static final ModConfigSpec.IntValue AF_OUTPUT_PER_TICK = BUILDER
            .comment("Maximum AF emitted from the top side each tick")
            .defineInRange("energyCore.outputPerTick", 80, 1, 10000);

    public static final ModConfigSpec.IntValue ROTATION_INPUT_PER_TICK = BUILDER
            .comment("Maximum rotational input accepted from the bottom side each tick")
            .defineInRange("energyCore.rotationInputPerTick", 100, 1, 10000);

    public static final ModConfigSpec.IntValue STRUCTURE_SCAN_INTERVAL = BUILDER
            .comment("Ticks between multiblock validation passes")
            .defineInRange("energyCore.structureScanInterval", 20, 1, 200);

    public static final ModConfigSpec.DoubleValue CORE_BREAK_EXPLOSION_POWER = BUILDER
            .comment("Explosion strength used when the controller is destroyed")
            .defineInRange("energyCore.coreBreakExplosionPower", 3.5D, 0.0D, 32.0D);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
