package org.polaris2023.caft.quantum;

public class QuantumEngineData {
    private int currentEnergy;
    private final int maxEnergy;
    private int energyConsumptionPerJump;

    public QuantumEngineData(int maxEnergy, int energyConsumptionPerJump) {
        this(maxEnergy, maxEnergy, energyConsumptionPerJump);
    }

    public QuantumEngineData(int currentEnergy, int maxEnergy, int energyConsumptionPerJump) {
        this.maxEnergy = Math.max(0, maxEnergy);
        this.currentEnergy = Math.max(0, Math.min(currentEnergy, this.maxEnergy));
        this.energyConsumptionPerJump = Math.max(0, energyConsumptionPerJump);
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getEnergyConsumptionPerJump() {
        return energyConsumptionPerJump;
    }

    public void setEnergyConsumptionPerJump(int energyConsumptionPerJump) {
        this.energyConsumptionPerJump = Math.max(0, energyConsumptionPerJump);
    }

    public boolean canJump() {
        return currentEnergy >= energyConsumptionPerJump;
    }

    public boolean consumeEnergy() {
        return consumeEnergy(energyConsumptionPerJump);
    }

    public boolean consumeEnergy(int amount) {
        int sanitizedAmount = Math.max(0, amount);
        if (currentEnergy < sanitizedAmount) {
            return false;
        }

        currentEnergy -= sanitizedAmount;
        return true;
    }

    public int rechargeOverTime(int rechargePerTick) {
        return receiveEnergy(rechargePerTick);
    }

    public int receiveEnergy(int amount) {
        int sanitizedAmount = Math.max(0, amount);
        int accepted = Math.min(sanitizedAmount, maxEnergy - currentEnergy);
        currentEnergy += accepted;
        return accepted;
    }

    public double getEnergyRatio() {
        if (maxEnergy <= 0) {
            return 0.0D;
        }
        return (double) currentEnergy / (double) maxEnergy;
    }
}
