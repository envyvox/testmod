package net.envyvox.testmod.thirst;

import net.minecraft.nbt.CompoundTag;

public class PlayerThirst {
    private int thirst;

    public int getThirst() {
        return thirst;
    }

    public void addThirst(int add) {
        int MAX_THIRST = 10;
        this.thirst = Math.min(thirst + add, MAX_THIRST);
    }

    public void subThirst(int add) {
        int MIN_THIRST = 0;
        this.thirst = Math.max(thirst - add, MIN_THIRST);
    }

    public void copyFrom(PlayerThirst source) {
        this.thirst = source.thirst;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("thist", thirst);
    }

    public void loadNBTData(CompoundTag nbt) {
        nbt.getInt("thirst");
    }
}
