package me.duquee.createutilities;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.DistExecutor;
import me.duquee.createutilities.blocks.CUBlocks;
import me.duquee.createutilities.blocks.CUTileEntities;
import me.duquee.createutilities.blocks.voidtypes.CUContainerTypes;
import me.duquee.createutilities.blocks.voidtypes.battery.VoidBatteryData;
import me.duquee.createutilities.blocks.voidtypes.chest.VoidChestInventoriesData;
import me.duquee.createutilities.blocks.voidtypes.motor.VoidMotorNetworkHandler;
import me.duquee.createutilities.blocks.voidtypes.tank.VoidTanksData;
import me.duquee.createutilities.events.CommonEvents;
import me.duquee.createutilities.items.CUItems;
import me.duquee.createutilities.mountedstorage.CUMountedStorages;
import me.duquee.createutilities.networking.CUPackets;
import me.duquee.createutilities.tabs.CUCreativeTabs;
import me.duquee.createutilities.voidlink.VoidLinkHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreateUtilities.ID)
public class CreateUtilities {

	public static final String ID = "createutilities";
	public static final String NAME = "Create Utilities";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

	public static final VoidMotorNetworkHandler VOID_MOTOR_LINK_NETWORK_HANDLER = new VoidMotorNetworkHandler();
	public static VoidChestInventoriesData VOID_CHEST_INVENTORIES_DATA;

	public static VoidTanksData VOID_TANKS_DATA;
	public static VoidBatteryData VOID_BATTERIES_DATA;

	public CreateUtilities(IEventBus modEventBus) {

		REGISTRATE.registerEventListeners(modEventBus);

		CUBlocks.register();
		CUItems.register();
		CUTileEntities.register();
		CUContainerTypes.register();
		CUCreativeTabs.register(modEventBus);
		CUMountedStorages.register();

		NeoForge.EVENT_BUS.register(CommonEvents.class);
		NeoForge.EVENT_BUS.register(VoidLinkHandler.class);

		modEventBus.addListener(CUPackets::register);
		modEventBus.addListener(CreateUtilities::registerCapabilities);
		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
			CreateUtilitiesClient.onCtorClient(modEventBus);
			return null;
		});

	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CUTileEntities.VOID_CHEST.get(),
				(voidChest, side) -> voidChest.getItemStorage());
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CUTileEntities.VOID_TANK.get(),
				(voidTank, side) -> voidTank.getFluidStorage());
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, CUTileEntities.VOID_BATTERY.get(),
				(voidBattery, side) -> voidBattery.getBattery());
	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
}
