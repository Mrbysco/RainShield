package com.mrbysco.rainshield.compat;

import com.mrbysco.rainshield.RainShield;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import tv.soaryn.simpleweather.api.SimpleWeatherAPI;
import tv.soaryn.simpleweather.api.WeatherType;
import tv.soaryn.volumetrics.utils.Cuboid;

public class SimpleWeatherCompat {
	public static void onAddition(ServerLevel serverLevel, BlockPos pos) {
		BlockPos minPos = pos.offset(-64, -64, -64);
		BlockPos maxPos = pos.offset(64, 64, 64);
		SimpleWeatherAPI.addDeterrenceVolume(serverLevel, pos.asLong(), new Cuboid(minPos.asLong(), maxPos.asLong()), RainShield.MOD_ID, WeatherType.ALL);
	}

	public static void onRemoval(ServerLevel serverLevel, BlockPos pos) {
		SimpleWeatherAPI.removeDeterrenceVolume(serverLevel, pos.asLong(), RainShield.MOD_ID);
	}
}
