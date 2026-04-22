package org.polaris2023.caft.quantum;

public final class QuantumPropulsionAttachments {
    private QuantumPropulsionAttachments() {
    }

    public static QuantumUpgradeProfile createShaftUpgrade() {
        return QuantumUpgradeProfile.forMount(QuantumMountType.CREATE_SHAFT);
    }

    public static QuantumUpgradeProfile elytraHarnessUpgrade() {
        return QuantumUpgradeProfile.forMount(QuantumMountType.ELYTRA_HARNESS);
    }

    public static QuantumUpgradeProfile backpackUpgrade() {
        return QuantumUpgradeProfile.forMount(QuantumMountType.BACKPACK_SLOT);
    }

    public static QuantumUpgradeProfile customAircraftUpgrade() {
        return QuantumUpgradeProfile.forMount(QuantumMountType.CUSTOM_AIRCRAFT);
    }
}
