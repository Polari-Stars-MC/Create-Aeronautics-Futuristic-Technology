package org.polaris2023.caft.content.energy;

import net.minecraft.nbt.CompoundTag;

public class FutureForceStorage {
    private int amount;
    private int capacity;

    public FutureForceStorage(int capacity) {
        this.capacity = capacity;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.amount = Math.min(this.amount, this.capacity);
    }

    public void setAmount(int amount) {
        this.amount = Math.clamp(amount, 0, this.capacity);
    }

    public int receive(int value, boolean simulate) {
        int accepted = Math.clamp(value, 0, this.capacity - this.amount);
        if (!simulate) {
            this.amount += accepted;
        }
        return accepted;
    }

    public int extract(int value, boolean simulate) {
        int extracted = Math.clamp(value, 0, this.amount);
        if (!simulate) {
            this.amount -= extracted;
        }
        return extracted;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Amount", this.amount);
        tag.putInt("Capacity", this.capacity);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.capacity = Math.max(1, tag.getInt("Capacity"));
        this.amount = Math.clamp(tag.getInt("Amount"), 0, this.capacity);
    }
}
