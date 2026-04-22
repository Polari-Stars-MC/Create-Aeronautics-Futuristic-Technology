package org.polaris2023.caft.quantum;

public record QuantumUpgradeProfile(
        float energyCostMultiplier,
        double precisionErrorCap,
        int cooldownReductionTicks) {

    public static final QuantumUpgradeProfile BASE = new QuantumUpgradeProfile(1.0F, 1.5D, 0);

    public QuantumUpgradeProfile {
        energyCostMultiplier = Math.max(0.1F, energyCostMultiplier);
        precisionErrorCap = Math.max(0.0D, precisionErrorCap);
        cooldownReductionTicks = Math.max(0, cooldownReductionTicks);
    }

    public static QuantumUpgradeProfile forMount(QuantumMountType mountType) {
        if (mountType == null) {
            return BASE;
        }
        return switch (mountType) {
            case CREATE_SHAFT -> new QuantumUpgradeProfile(0.92F, 0.45D, 10);
            case ELYTRA_HARNESS -> new QuantumUpgradeProfile(0.88F, 0.40D, 16);
            case BACKPACK_SLOT -> new QuantumUpgradeProfile(0.90F, 0.35D, 12);
            case CUSTOM_AIRCRAFT -> new QuantumUpgradeProfile(0.84F, 0.25D, 24);
        };
    }

    public int applyEnergyCost(int baseCost) {
        return Math.max(1, Math.round(baseCost * energyCostMultiplier));
    }

    public double clampPrecision(double basePrecision) {
        return Math.min(basePrecision, precisionErrorCap);
    }

    public int applyCooldownReduction(int baseCooldownTicks) {
        return Math.max(0, baseCooldownTicks - cooldownReductionTicks);
    }
}
