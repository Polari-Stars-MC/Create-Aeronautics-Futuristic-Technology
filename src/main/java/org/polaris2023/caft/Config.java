package org.polaris2023.caft;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BLINK_BASE_ENERGY_COST;
    public static final ModConfigSpec.IntValue SURGE_BASE_ENERGY_COST;
    public static final ModConfigSpec.IntValue BLINK_MAX_DISTANCE;
    public static final ModConfigSpec.IntValue SURGE_SPEED_MULTIPLIER;
    public static final ModConfigSpec.IntValue DEFAULT_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue MAX_FAILURE_CHANCE;
    public static final ModConfigSpec.DoubleValue PRECISION_ERROR_MIN;
    public static final ModConfigSpec.DoubleValue PRECISION_ERROR_MAX;
    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_NAVIGATION;
    public static final ModConfigSpec.BooleanValue ENABLE_BACKLASH_DAMAGE;
    public static final ModConfigSpec.BooleanValue PRESERVE_MOMENTUM_ON_BLINK;
    public static final ModConfigSpec.DoubleValue LOW_ENERGY_FAILURE_THRESHOLD;

    static {
        BUILDER.push("quantumPropulsion");

        BLINK_BASE_ENERGY_COST = BUILDER
                .comment("Base FE/RF cost for Blink Drive before distance, load, and instability modifiers.")
                .defineInRange("blinkBaseEnergyCost", 280, 200, 400);

        SURGE_BASE_ENERGY_COST = BUILDER
                .comment("Base FE/RF cost for Surge Drive before thrust and momentum modifiers.")
                .defineInRange("surgeBaseEnergyCost", 220, 200, 400);

        BLINK_MAX_DISTANCE = BUILDER
                .comment("Default maximum blink distance in blocks before upgrades and load penalties.")
                .defineInRange("blinkMaxDistance", 20, 16, 32);

        SURGE_SPEED_MULTIPLIER = BUILDER
                .comment("Percent speed increase granted by the default Surge Drive profile.")
                .defineInRange("surgeSpeedMultiplier", 180, 100, 300);

        DEFAULT_COOLDOWN_TICKS = BUILDER
                .comment("Shared base cooldown for QPS actions, in ticks.")
                .defineInRange("defaultCooldownTicks", 120, 60, 160);

        MAX_FAILURE_CHANCE = BUILDER
                .comment("Upper bound for instability-driven failures. 0.25 means 25 percent.")
                .defineInRange("maxFailureChance", 0.25D, 0.0D, 0.25D);

        PRECISION_ERROR_MIN = BUILDER
                .comment("Lowest achievable landing error radius for a fully stabilized Blink Drive.")
                .defineInRange("precisionErrorMin", 0.5D, 0.0D, 2.0D);

        PRECISION_ERROR_MAX = BUILDER
                .comment("Highest default landing error radius when stabilization is poor.")
                .defineInRange("precisionErrorMax", 2.0D, 0.5D, 4.0D);

        ENABLE_AUTO_NAVIGATION = BUILDER
                .comment("Allows fixed-heading blink execution for engineering corridors and rail lines.")
                .define("enableAutoNavigation", true);

        ENABLE_BACKLASH_DAMAGE = BUILDER
                .comment("Whether energy backlash can convert into direct damage on failure.")
                .define("enableBacklashDamage", true);

        PRESERVE_MOMENTUM_ON_BLINK = BUILDER
                .comment("If true, Blink keeps the player's incoming motion. If false, motion is reset after the jump.")
                .define("preserveMomentumOnBlink", true);

        LOW_ENERGY_FAILURE_THRESHOLD = BUILDER
                .comment("Energy ratio below which failure chance ramps up sharply. 0.30 means 30 percent.")
                .defineInRange("lowEnergyFailureThreshold", 0.30D, 0.0D, 1.0D);

        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

}
