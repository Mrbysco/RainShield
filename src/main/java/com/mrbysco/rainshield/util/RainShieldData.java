package com.mrbysco.rainshield.util;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.block.RainShieldBlock;
import com.mrbysco.rainshield.client.RainShieldConfig;
import com.mrbysco.rainshield.handler.SyncHandler;
import com.mrbysco.rainshield.network.PacketHandler;
import com.mrbysco.rainshield.network.message.SyncShieldMapMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RainShieldData extends SavedData {
	private static final String DATA_NAME = RainShield.MOD_ID + "_world_data";

	public static Map<ResourceLocation, List<BlockPos>> rainShieldMap = new HashMap<>();

	public RainShieldData(Map<ResourceLocation, List<BlockPos>> rainShieldMap) {
		RainShieldData.rainShieldMap = rainShieldMap;
	}

	public RainShieldData() {
		this(new HashMap<>());
	}

	public static void addRainShieldPos(BlockPos pos, Level level) {
		ResourceLocation dimensionLocation = level.dimension().location();
		List<BlockPos> blockPositions = rainShieldMap.getOrDefault(dimensionLocation, new ArrayList<>());
		blockPositions.add(pos);

		rainShieldMap.put(dimensionLocation, blockPositions);

		if (!level.isClientSide) {
			RainShieldData data = get(level);
			data.setDirty(true);
			((ServerLevel) level).players().forEach(SyncHandler::syncShieldMap);
		}
	}

	public static void removeRainShieldPos(BlockPos pos, Level level) {
		ResourceLocation dimensionLocation = level.dimension().location();
		List<BlockPos> blockPositions = rainShieldMap.getOrDefault(dimensionLocation, new ArrayList<>());

		if (!blockPositions.isEmpty()) {
			blockPositions.removeIf(position -> position.equals(pos));
			rainShieldMap.put(dimensionLocation, blockPositions);

			if (!level.isClientSide) {
				RainShieldData data = get(level);
				data.setDirty(true);
			}
		}
	}

	public static boolean cancelRain(Level level, BlockPos pos) {
		if (level != null) {
			ResourceLocation dimensionLocation = level.dimension().location();
			if (rainShieldMap.containsKey(dimensionLocation)) {
				List<BlockPos> blockPositions = rainShieldMap.get(dimensionLocation);
				for (BlockPos shieldPos : blockPositions) {
					if (!level.isAreaLoaded(shieldPos, 1)) continue;

					double distance = pos.distManhattan(shieldPos);
					if (distance <= RainShieldConfig.COMMON.rainShieldDistance.get()) {
						BlockState state = level.getBlockState(shieldPos);
						if (state.getBlock() instanceof RainShieldBlock && !state.getValue(RainShieldBlock.POWERED)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static RainShieldData load(CompoundTag tag) {
		PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncShieldMapMessage(tag));

		ListTag rainShieldMap = tag.getList("RainShieldMap", CompoundTag.TAG_COMPOUND);
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

		return new RainShieldData(shieldMap);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag rainShieldList = new ListTag();
		for (Map.Entry<ResourceLocation, List<BlockPos>> entry : rainShieldMap.entrySet()) {
			CompoundTag shieldTag = new CompoundTag();
			shieldTag.putString("Dimension", entry.getKey().toString());

			ListTag blockPositions = new ListTag();
			for (BlockPos pos : entry.getValue()) {
				CompoundTag blockPosTag = new CompoundTag();
				blockPosTag.putLong("BlockPos", pos.asLong());
				blockPositions.add(blockPosTag);
			}
			shieldTag.put("BlockPositions", blockPositions);

			rainShieldList.add(shieldTag);
		}
		tag.put("RainShieldMap", rainShieldList);

		return tag;
	}

	public static RainShieldData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(new Factory<>(RainShieldData::new, RainShieldData::load), DATA_NAME);
	}
}
