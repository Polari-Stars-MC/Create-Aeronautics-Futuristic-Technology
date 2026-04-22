package org.polaris2023.caft.quantum;

public interface IQuantumUpgradeCarrier {
    QuantumMountType getQuantumMountType();

    default QuantumUpgradeProfile getQuantumUpgradeProfile() {
        return QuantumUpgradeProfile.forMount(getQuantumMountType());
    }
}
