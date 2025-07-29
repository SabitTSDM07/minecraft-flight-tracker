package com.SabitTSDM07.flighttracker.entity;

import com.SabitTSDM07.flighttracker.PlaneData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class PlaneEntity extends Entity {

    private PlaneData planeData;

    public PlaneEntity(EntityType<? extends PlaneEntity> type, Level level) {
        super(type, level);
    }

    public void setPlaneData(PlaneData data) {
        this.planeData = data;
    }

    @Nullable
    public PlaneData getPlaneData() {
        return this.planeData;
    }

    @Override
    protected void defineSynchedData() {
        // No synced data for now
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Not saving PlaneData yet — optional
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // Not saving PlaneData yet — optional
    }
}
