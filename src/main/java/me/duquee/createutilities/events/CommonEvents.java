package me.duquee.createutilities.events;

import me.duquee.createutilities.CreateUtilities;
import me.duquee.createutilities.blocks.voidtypes.battery.VoidBatteryData;
import me.duquee.createutilities.blocks.voidtypes.chest.VoidChestInventoriesData;
import me.duquee.createutilities.blocks.voidtypes.tank.VoidTanksData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public class CommonEvents {

	@SubscribeEvent
	public static void onLoad(LevelEvent.Load event) {

		MinecraftServer server = event.getLevel().getServer();
		if (server == null) return;

		LevelAccessor level = event.getLevel();
		DimensionDataStorage dataStorage = server.overworld().getDataStorage();

		CreateUtilities.VOID_MOTOR_LINK_NETWORK_HANDLER.onLoadWorld(level);

		CreateUtilities.VOID_CHEST_INVENTORIES_DATA = dataStorage
				.computeIfAbsent(VoidChestInventoriesData.factory(DataFixTypes.LEVEL), "VoidChestInventories");

		CreateUtilities.VOID_TANKS_DATA = dataStorage
				.computeIfAbsent(VoidTanksData.factory(DataFixTypes.LEVEL), "VoidTanks");

		CreateUtilities.VOID_BATTERIES_DATA = dataStorage
				.computeIfAbsent(VoidBatteryData.factory(DataFixTypes.LEVEL), "VoidBatteries");

	}

	@SubscribeEvent
	public static void onUnload(LevelEvent.Unload event) {
		CreateUtilities.VOID_MOTOR_LINK_NETWORK_HANDLER.onUnloadWorld(event.getLevel());
	}

}
