package me.duquee.createutilities.networking.packets;

import com.simibubi.create.foundation.utility.DistExecutor;
import me.duquee.createutilities.CreateUtilities;
import me.duquee.createutilities.CreateUtilitiesClient;
import me.duquee.createutilities.blocks.voidtypes.motor.VoidMotorNetworkHandler.NetworkKey;
import me.duquee.createutilities.blocks.voidtypes.tank.VoidTank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VoidTankUpdatePacket implements CustomPacketPayload {

	public static final Type<VoidTankUpdatePacket> TYPE = new Type<>(CreateUtilities.asResource("void_tank_update"));
	public static final StreamCodec<RegistryFriendlyByteBuf, VoidTankUpdatePacket> STREAM_CODEC =
			StreamCodec.ofMember(VoidTankUpdatePacket::write, VoidTankUpdatePacket::new);

	private final NetworkKey key;
	private final FluidTank tank;

	public VoidTankUpdatePacket(NetworkKey key, VoidTank tank) {
		this.key = key;
		this.tank = tank;
	}

	public VoidTankUpdatePacket(RegistryFriendlyByteBuf buffer) {
		key = NetworkKey.fromBuffer(buffer);
		tank = new FluidTank(VoidTank.CAPACITY);
		CompoundTag tag = buffer.readNbt();
		if (tag != null) {
			tank.readFromNBT(buffer.registryAccess(), tag);
		}
	}

	private void write(RegistryFriendlyByteBuf buffer) {
		key.writeToBuffer(buffer);
		buffer.writeNbt(tank.writeToNBT(buffer.registryAccess(), new CompoundTag()));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(VoidTankUpdatePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
			CreateUtilitiesClient.VOID_TANKS.storages.put(packet.key, packet.tank);
			return null;
		}));
	}

}
