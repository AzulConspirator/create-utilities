package me.duquee.createutilities.networking.packets;

import com.simibubi.create.foundation.utility.DistExecutor;
import me.duquee.createutilities.CreateUtilities;
import me.duquee.createutilities.CreateUtilitiesClient;
import me.duquee.createutilities.blocks.voidtypes.battery.VoidBattery;
import me.duquee.createutilities.blocks.voidtypes.motor.VoidMotorNetworkHandler.NetworkKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VoidBatteryUpdatePacket implements CustomPacketPayload {

	public static final Type<VoidBatteryUpdatePacket> TYPE = new Type<>(CreateUtilities.asResource("void_battery_update"));
	public static final StreamCodec<RegistryFriendlyByteBuf, VoidBatteryUpdatePacket> STREAM_CODEC =
			StreamCodec.ofMember(VoidBatteryUpdatePacket::write, VoidBatteryUpdatePacket::new);

	private final NetworkKey key;
	private final VoidBattery battery;

	public VoidBatteryUpdatePacket(NetworkKey key, VoidBattery battery) {
		this.key = key;
		this.battery = battery;
	}

	public VoidBatteryUpdatePacket(RegistryFriendlyByteBuf buffer) {
		key = NetworkKey.fromBuffer(buffer);
		battery = new VoidBattery(key);
		battery.deserializeNBT(buffer.readNbt());
	}

	private void write(RegistryFriendlyByteBuf buffer) {
		key.writeToBuffer(buffer);
		buffer.writeNbt(battery.serializeNBT());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(VoidBatteryUpdatePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
			CreateUtilitiesClient.VOID_BATTERIES.storages.put(packet.key, packet.battery);
			return null;
		}));
	}

}
