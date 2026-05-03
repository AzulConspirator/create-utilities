package me.duquee.createutilities.blocks.voidtypes.battery;

import org.jetbrains.annotations.NotNull;

import me.duquee.createutilities.blocks.voidtypes.VoidStorageData;
import me.duquee.createutilities.blocks.voidtypes.motor.VoidMotorNetworkHandler.NetworkKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class VoidBatteryData extends VoidStorageData<VoidBattery> {

	public VoidBattery computeStorageIfAbsent(NetworkKey key) {
		return super.computeStorageIfAbsent(key, VoidBattery::new);
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
		return super.save(tag, registries, VoidBattery::isEmpty,
				(battery, provider) -> battery.serializeNBT());
	}

	public static VoidBatteryData load(CompoundTag tag, HolderLookup.Provider registries) {
		return load(tag, registries, VoidBatteryData::new, VoidBattery::new,
				VoidBattery::deserializeNBT);
	}

	public static SavedData.Factory<VoidBatteryData> factory(DataFixTypes dataFixType) {
		return new SavedData.Factory<>(VoidBatteryData::new, VoidBatteryData::load, dataFixType);
	}

}
