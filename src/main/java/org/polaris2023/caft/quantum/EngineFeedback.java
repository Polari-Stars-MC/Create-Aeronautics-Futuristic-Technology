package org.polaris2023.caft.quantum;

public record EngineFeedback(
        EngineStatus status,
        String message,
        float energyRatio,
        int remainingCooldownTicks,
        double predictedPrecisionError,
        double failureChance) {

    public static EngineFeedback ready(float energyRatio, double predictedPrecisionError) {
        return new EngineFeedback(
                EngineStatus.READY,
                "Quantum engine synchronized.",
                energyRatio,
                0,
                predictedPrecisionError,
                0.0D
        );
    }

    public static EngineFeedback cooling(float energyRatio, int remainingCooldownTicks) {
        return new EngineFeedback(
                EngineStatus.COOLING_DOWN,
                "Quantum engine cooling down.",
                energyRatio,
                remainingCooldownTicks,
                0.0D,
                0.0D
        );
    }

    public static EngineFeedback insufficientEnergy(float energyRatio, double failureChance) {
        return new EngineFeedback(
                EngineStatus.INSUFFICIENT_ENERGY,
                "Quantum engine charge below jump threshold.",
                energyRatio,
                0,
                0.0D,
                failureChance
        );
    }

    public static EngineFeedback faulted(float energyRatio, int remainingCooldownTicks, double failureChance) {
        return new EngineFeedback(
                EngineStatus.FAULTED,
                "Quantum engine destabilized.",
                energyRatio,
                remainingCooldownTicks,
                0.0D,
                failureChance
        );
    }
}
