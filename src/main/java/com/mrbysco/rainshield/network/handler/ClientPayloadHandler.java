package com.mrbysco.rainshield.network.handler;

import com.mrbysco.rainshield.network.payloads.SyncShieldMapPayload;
import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final SyncShieldMapPayload payload, final IPayloadContext context) {
		context.enqueueWork(() -> {
					RegistryOps<Tag> nbtOps = context.player().registryAccess().createSerializationContext(NbtOps.INSTANCE);
					Optional<Map<ResourceKey<Level>, List<BlockPos>>> optionalMap = payload.shieldMapTag().read("RainShieldMap", RainShieldData.MAP_CODEC, nbtOps);
					if (optionalMap.isPresent()) {
						RainShieldData.rainShieldMap.clear();
						RainShieldData.rainShieldMap.putAll(optionalMap.get());
					} else {
						context.disconnect(Component.translatable("rainshield.networking.sync_shields.failed", "Invalid data format"));
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("rainshield.networking.sync_shields.failed", e.getMessage()));
					return null;
				});
	}
}
