package com.mrbysco.rainshield.client;

import com.mrbysco.rainshield.block.RainShieldBlock;
import com.mrbysco.rainshield.config.RainShieldConfig;
import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RainHelper {

	public static boolean cancelRain(BlockPos pos) {
		Level level = Minecraft.getInstance().level;
		if (level != null) {
			ResourceKey<Level> dimension = level.dimension();
			if (RainShieldData.rainShieldMap.containsKey(dimension)) {
				List<BlockPos> blockPositions = new ArrayList<>(RainShieldData.rainShieldMap.getOrDefault(dimension, new ArrayList<>()));
				for (BlockPos shieldPos : blockPositions) {
					if (!level.isAreaLoaded(shieldPos, 1)) continue;

					double distance = pos.distManhattan(shieldPos);
					if (distance <= RainShieldConfig.CLIENT.rainShieldDistance.get()) {
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
}
