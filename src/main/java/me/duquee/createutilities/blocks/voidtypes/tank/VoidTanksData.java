package me.duquee.createutilities.blocks.voidtypes.tank;

import me.duquee.createutilities.blocks.voidtypes.VoidStorageData;
import me.duquee.createutilities.blocks.voidtypes.motor.VoidMotorNetworkHandler.NetworkKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

public class VoidTanksData extends VoidStorageData<VoidTank> {

	public VoidTank computeStorageIfAbsent(NetworkKey key) {
		return super.computeStorageIfAbsent(key, VoidTank::new);
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
		return super.save(tag, registries, VoidTank::isEmpty,
				(tank, provider) -> tank.writeToNBT(provider, new CompoundTag()));
	}

	public static VoidTanksData load(CompoundTag tag, HolderLookup.Provider registries) {
		return load(tag, registries, VoidTanksData::new, VoidTank::new,
				(tank, data) -> tank.readFromNBT(registries, data));
	}

	public static SavedData.Factory<VoidTanksData> factory(DataFixTypes dataFixType) {
		return new SavedData.Factory<>(VoidTanksData::new, VoidTanksData::load, dataFixType);
	}

}
