package com.mrbysco.rainshield.network.message;

import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncShieldMapMessage {

	private final CompoundTag shieldMapTag;

	public SyncShieldMapMessage(CompoundTag tag) {
		this.shieldMapTag = tag;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(shieldMapTag);
	}

	public static SyncShieldMapMessage decode(final FriendlyByteBuf buffer) {
		return new SyncShieldMapMessage(buffer.readNbt());
	}

	public void handle(Context ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				if (FMLEnvironment.dist.isClient()) {
					ListTag rainShieldMap = shieldMapTag.getList("RainShieldMap", CompoundTag.TAG_COMPOUND);
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
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
