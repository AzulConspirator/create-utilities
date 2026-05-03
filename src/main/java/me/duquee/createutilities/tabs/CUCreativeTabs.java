package me.duquee.createutilities.tabs;

import me.duquee.createutilities.CreateUtilities;
import me.duquee.createutilities.blocks.CUBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUCreativeTabs {

	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, CreateUtilities.ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE = TAB_REGISTER.register("base",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.createutilities.base"))
					.icon(CUBlocks.VOID_MOTOR::asStack)
					.build());

	public static void register(IEventBus modEventBus) {
		TAB_REGISTER.register(modEventBus);
	}
}
