package me.duquee.createutilities.blocks.voidtypes.motor;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import me.duquee.createutilities.CreateUtilities;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.*;

public class VoidMotorNetworkHandler {

	static final Map<LevelAccessor, Map<NetworkKey, Set<BlockPos>>> connections =
			new IdentityHashMap<>();

	public Set<BlockPos> getNetworkOf(LevelAccessor world, VoidMotorLinkBehaviour actor) {
		Map<NetworkKey, Set<BlockPos>> networksInWorld = networksIn(world);
		NetworkKey key = actor.getNetworkKey();
		if (!networksInWorld.containsKey(key))
			networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public Map<NetworkKey, Set<BlockPos>> networksIn(LevelAccessor world) {
		if (!connections.containsKey(world)) {
			Create.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
			return new HashMap<>();
		}
		return connections.get(world);
	}

	public void onLoadWorld(LevelAccessor world) {
		connections.put(world, new HashMap<>());
		Create.LOGGER.debug("Prepared Void Motor Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void onUnloadWorld(LevelAccessor world) {
		connections.remove(world);
		Create.LOGGER.debug("Removed Void Motor Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void addToNetwork(LevelAccessor world, VoidMotorLinkBehaviour actor) {
		getNetworkOf(world, actor).add(actor.getPos());
		if (actor.blockEntity instanceof VoidMotorTileEntity voidMotor) voidMotor.onConnectToVoidNetwork();
	}

	public void removeFromNetwork(LevelAccessor world, VoidMotorLinkBehaviour actor) {
		if (actor.blockEntity instanceof VoidMotorTileEntity voidMotor) voidMotor.onDisconnectFromVoidNetwork();
		Set<BlockPos> network = getNetworkOf(world, actor);
		network.remove(actor.getPos());
		if (network.isEmpty()) networksIn(world).remove(actor.getNetworkKey());
	}

	public static class NetworkKey {

		@Nullable
		public final GameProfile owner;
		public final Couple<Frequency> frequencies;

		public NetworkKey(@Nullable GameProfile owner, Frequency frequencyFirst, Frequency frequencySecond) {
			this.owner = owner;
			this.frequencies = Couple.create(frequencyFirst, frequencySecond);
		}

		public void writeToBuffer(FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(frequencies.get(true).getStack().getItem()));
			buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(frequencies.get(false).getStack().getItem()));
			buffer.writeBoolean(owner != null);
			if (owner != null) {
				buffer.writeBoolean(owner.getId() != null);
				if (owner.getId() != null) {
					buffer.writeUUID(owner.getId());
				}
				buffer.writeUtf(owner.getName() == null ? "" : owner.getName());
			}
		}

		public static NetworkKey fromBuffer(FriendlyByteBuf buffer) {
			ItemStack frequencyFirst = new ItemStack(BuiltInRegistries.ITEM.get(buffer.readResourceLocation()));
			ItemStack frequencyLast = new ItemStack(BuiltInRegistries.ITEM.get(buffer.readResourceLocation()));
			GameProfile owner = null;
			if (buffer.readBoolean()) {
				UUID ownerId = buffer.readBoolean() ? buffer.readUUID() : null;
				String ownerName = buffer.readUtf();
				owner = new GameProfile(ownerId, ownerName.isBlank() ? null : ownerName);
			}
			return new NetworkKey(owner, Frequency.of(frequencyFirst), Frequency.of(frequencyLast));
		}

		@Override
		public int hashCode() {
			return Objects.hash(owner, frequencies);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			NetworkKey other = (NetworkKey) obj;
			return Objects.equals(owner, other.owner) && frequencies.equals(other.frequencies);
		}

		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			if (owner != null) {
				if (owner.getId() != null) {
					tag.putUUID("OwnerId", owner.getId());
				}
				if (owner.getName() != null) {
					tag.putString("OwnerName", owner.getName());
				}
			}
			tag.put("FrequencyFirst", serializeFrequency(frequencies.get(true).getStack()));
			tag.put("FrequencyLast", serializeFrequency(frequencies.get(false).getStack()));
			return tag;
		}

		public static NetworkKey deserialize(CompoundTag tag) {
			Frequency frequencyFirst = Frequency.of(deserializeFrequency(tag.getCompound("FrequencyFirst")));
			Frequency frequencyLast = Frequency.of(deserializeFrequency(tag.getCompound("FrequencyLast")));
			GameProfile owner = null;
			if (tag.contains("OwnerId") || tag.contains("OwnerName")) {
				owner = new GameProfile(tag.hasUUID("OwnerId") ? tag.getUUID("OwnerId") : null,
						tag.contains("OwnerName") ? tag.getString("OwnerName") : null);
			}
			return new NetworkKey(owner, frequencyFirst, frequencyLast);
		}

		private static CompoundTag serializeFrequency(ItemStack stack) {
			CompoundTag tag = new CompoundTag();
			ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
			tag.putString("id", itemId.toString());
			if (stack.getCount() != 1) {
				tag.putInt("Count", stack.getCount());
			}
			return tag;
		}

		private static ItemStack deserializeFrequency(CompoundTag tag) {
			Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("id")));
			int count = tag.contains("Count") ? tag.getInt("Count") : 1;
			return new ItemStack(item, count);
		}

		@Override
		public String toString() {
			return serialize().toString();
		}

		public static NetworkKey fromString(String json) {

			CompoundTag tag;
			try {
				tag = TagParser.parseTag(json);
			} catch (CommandSyntaxException e) {
				CreateUtilities.LOGGER.error("Tried to load invalid NetworkKey '" + json + "'");
				return null;
			}

			return deserialize(tag);
		}

	}

}
