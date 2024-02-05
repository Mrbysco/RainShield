package com.mrbysco.rainshield.network.payloads;

import com.mrbysco.rainshield.RainShield;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncShieldMapPayload(CompoundTag shieldMapTag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(RainShield.MOD_ID, "sync_shields");

	public SyncShieldMapPayload(final FriendlyByteBuf buffer) {
		this(buffer.readNbt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(shieldMapTag);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
