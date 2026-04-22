package org.polaris2023.caft.quantum;

import java.util.List;

public record QuantumPropulsionBlueprint(
        String systemName,
        List<QuantumDriveProfile> driveProfiles,
        List<String> lore,
        QuantumBalanceFramework balance,
        QuantumGameplayFramework gameplay,
        QuantumAudioVisualDirection audioVisualDirection,
        QuantumModularFramework modularFramework) {

    public record QuantumDriveProfile(
            QuantumDriveType type,
            String title,
            String purpose,
            String mechanicalIdentity) {
    }

    public record QuantumBalanceFramework(
            ValueRange baseEnergyCost,
            ValueRange maxJumpDistance,
            ValueRange cooldownSeconds,
            ValueRange instabilityChance,
            ValueRange precisionError,
            List<String> formulas,
            List<String> recommendedDefaults) {
    }

    public record QuantumGameplayFramework(
            List<QuantumControlMode> controlModes,
            String directionRule,
            String autoNavigationRule,
            List<String> physicalInteractionRules,
            List<QuantumFailureConsequence> failureConsequences,
            List<String> riskRewardNotes) {
    }

    public record QuantumAudioVisualDirection(
            List<String> startupVisuals,
            List<String> transitVisuals,
            List<String> audioStages,
            List<String> immersionNotes) {
    }

    public record QuantumModularFramework(
            String baseUpgradePath,
            List<String> chassisComponents,
            List<QuantumUpgradeModule> upgradeModules,
            List<String> upgradeEffects) {
    }

    public record ValueRange(double min, double max, String unit, String note) {
    }
}
