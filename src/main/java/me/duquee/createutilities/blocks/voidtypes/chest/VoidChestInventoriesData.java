package me.duquee.createutilities.blocks.voidtypes.chest;

import me.duquee.createutilities.blocks.voidtypes.VoidStorageData;
import me.duquee.createutilities.blocks.voidtypes.motor.VoidMotorNetworkHandler.NetworkKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

public class VoidChestInventoriesData extends VoidStorageData<VoidChestInventory> {

	public VoidChestInventory computeStorageIfAbsent(NetworkKey key) {
		return super.computeStorageIfAbsent(key, VoidChestInventory::new);
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
		return super.save(tag, registries, VoidChestInventory::isEmpty, VoidChestInventory::serializeNBT);
	}

	public static VoidChestInventoriesData load(CompoundTag tag, HolderLookup.Provider registries) {
		return load(tag, registries, VoidChestInventoriesData::new, VoidChestInventory::new,
				(inventory, data) -> inventory.deserializeNBT(registries, data));
	}

	public static SavedData.Factory<VoidChestInventoriesData> factory(DataFixTypes dataFixType) {
		return new SavedData.Factory<>(VoidChestInventoriesData::new, VoidChestInventoriesData::load, dataFixType);
	}

}
