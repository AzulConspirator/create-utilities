package me.duquee.createutilities.networking;

import me.duquee.createutilities.networking.packets.VoidBatteryUpdatePacket;
import me.duquee.createutilities.networking.packets.VoidTankUpdatePacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class CUPackets {

	public static final String NETWORK_VERSION_STR = "2";

	private CUPackets() {}

	public static void register(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar(NETWORK_VERSION_STR);
		registrar.playToClient(VoidTankUpdatePacket.TYPE, VoidTankUpdatePacket.STREAM_CODEC, VoidTankUpdatePacket::handle);
		registrar.playToClient(VoidBatteryUpdatePacket.TYPE, VoidBatteryUpdatePacket.STREAM_CODEC, VoidBatteryUpdatePacket::handle);
	}

}
