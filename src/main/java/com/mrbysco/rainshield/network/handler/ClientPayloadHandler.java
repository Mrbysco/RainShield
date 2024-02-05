package com.mrbysco.rainshield.network.handler;

import com.mrbysco.rainshield.network.payloads.SyncShieldMapPayload;
import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final SyncShieldMapPayload payload, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					ListTag rainShieldMap = payload.shieldMapTag().getList("RainShieldMap", CompoundTag.TAG_COMPOUND);
					Map<ResourceLocation, List<BlockPos>> shieldMap = new HashMap<>();

					for (int i = 0; i < rainShieldMap.size(); ++i) {
						CompoundTag listTag = rainShieldMap.getCompound(i);
						String dimension = listTag.getString("Dimension");
						ResourceLocation dimensionLocation = ResourceLocation.tryParse(dimension);

						List<BlockPos> blockPositionsList = new ArrayList<>();
						ListTag blockPositions = listTag.getList("BlockPositions", ListTag.TAG_COMPOUND);
						for (int j = 0; j < blockPositions.size(); ++j) {
							CompoundTag blockPosTag = blockPositions.getCompound(j);
							BlockPos pos = BlockPos.of(blockPosTag.getLong("BlockPos"));
							blockPositionsList.add(pos);
						}
						shieldMap.put(dimensionLocation, blockPositionsList);
					}

					RainShieldData.rainShieldMap.clear();
					RainShieldData.rainShieldMap.putAll(shieldMap);
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("rainshield.networking.sync_shields.failed", e.getMessage()));
					return null;
				});
	}
}
