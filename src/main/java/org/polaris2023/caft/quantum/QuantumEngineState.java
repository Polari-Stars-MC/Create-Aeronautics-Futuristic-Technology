package org.polaris2023.caft.quantum;

public record QuantumEngineState(
        QuantumEngineData energyData,
        double durabilityRatio,
        int damageLevel,
        int overheatTicks) {

    public QuantumEngineState {
        durabilityRatio = clamp(durabilityRatio, 0.0D, 1.0D);
        damageLevel = Math.max(0, damageLevel);
        overheatTicks = Math.max(0, overheatTicks);
    }

    public double energyRatio() {
        return energyData == null ? 0.0D : energyData.getEnergyRatio();
    }

    public boolean isLowEnergy(double threshold) {
        return energyRatio() < threshold;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
