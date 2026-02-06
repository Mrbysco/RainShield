package com.mrbysco.rainshield.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.compat.SimpleWeatherCompat;
import com.mrbysco.rainshield.handler.SyncHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RainShieldData extends SavedData {
	private static final String DATA_NAME = RainShield.MOD_ID + "_world_data";

	public static final Codec<Map<ResourceKey<Level>, List<BlockPos>>> MAP_CODEC = Codec.unboundedMap(
			Level.RESOURCE_KEY_CODEC, BlockPos.CODEC.listOf()
	);
	public static final Codec<RainShieldData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
					MAP_CODEC.fieldOf("rainShieldMap").forGetter(data -> data.rainShieldMap))
			.apply(inst, RainShieldData::new));

	public static final Map<ResourceKey<Level>, List<BlockPos>> rainShieldMap = new HashMap<>();

	public RainShieldData(Map<ResourceKey<Level>, List<BlockPos>> rainShieldMap) {
		RainShieldData.rainShieldMap.clear();
		RainShieldData.rainShieldMap.putAll(rainShieldMap);
	}

	public RainShieldData() {
		this(new HashMap<>());
	}

	public static void addRainShieldPos(BlockPos pos, Level level) {
		ResourceKey<Level> dimension = level.dimension();
		List<BlockPos> blockPositions = new ArrayList<>(rainShieldMap.getOrDefault(dimension, new ArrayList<>()));
		if (!blockPositions.contains(pos)) {
			blockPositions.add(pos);
		}

		rainShieldMap.put(dimension, blockPositions);

		if (level instanceof ServerLevel serverLevel) {
			RainShieldData data = get(serverLevel);
			data.setDirty();
			serverLevel.players().forEach(SyncHandler::syncShieldMap);
			if (ModList.get().isLoaded("simple_weather")) {
				SimpleWeatherCompat.onAddition(serverLevel, pos);
			}
		}
	}

	public static void removeRainShieldPos(BlockPos pos, Level level) {
		ResourceKey<Level> dimension = level.dimension();
		List<BlockPos> blockPositions = new ArrayList<>(rainShieldMap.getOrDefault(dimension, new ArrayList<>()));
		boolean changed = false;

		if (!blockPositions.isEmpty()) {
			boolean removed = blockPositions.removeIf(position -> position.equals(pos));
			if (removed) {
				rainShieldMap.put(dimension, blockPositions);
				changed = true;
			}
		}

		if (level instanceof ServerLevel serverLevel) {
			if (changed) {
				RainShieldData data = get(level);
				data.setDirty();
			}
			if (ModList.get().isLoaded("simple_weather")) {
				SimpleWeatherCompat.onRemoval(serverLevel, pos);
			}
		}
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		DynamicOps<Tag> nbtOps = registries.createSerializationContext(NbtOps.INSTANCE);
		tag.store("RainShieldMap", MAP_CODEC, nbtOps, rainShieldMap);
		return tag;
	}

	public static SavedDataType<RainShieldData> type() {
		return new SavedDataType<>(DATA_NAME, RainShieldData::new, CODEC, null);
	}

	public static RainShieldData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}
}
