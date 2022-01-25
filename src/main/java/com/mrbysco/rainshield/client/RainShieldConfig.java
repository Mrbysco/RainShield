package com.mrbysco.rainshield.client;

import com.mrbysco.rainshield.RainShield;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class RainShieldConfig {

	public static class Common {
		public final IntValue rainShieldDistance;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			// Enable/Disable
			rainShieldDistance = builder
					.comment("Defines the range in which the Rain Shield stops rendering rain [default: 80]")
					.defineInRange("rainShieldDistance", 80, 1, Integer.MAX_VALUE);

			builder.pop();
		}
	}

	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		RainShield.LOGGER.debug("Loaded Rain Shield's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		RainShield.LOGGER.debug("Rain Shield's config just got changed on the file system!");
	}
}
