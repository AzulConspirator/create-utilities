package me.duquee.createutilities.events;

import me.duquee.createutilities.voidlink.VoidLinkRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientEvents {

	@SubscribeEvent
	public static void onTick(ClientTickEvent.Post event) {
		if (!isGameActive()) return;
		VoidLinkRenderer.tick();
	}

	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}

}
