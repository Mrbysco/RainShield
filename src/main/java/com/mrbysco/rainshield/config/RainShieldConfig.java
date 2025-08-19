package com.mrbysco.rainshield.config;

import com.mrbysco.rainshield.RainShield;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class RainShieldConfig {

	public static class Client {
		public final IntValue rainShieldDistance;

		Client(ModConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("client");

			// Enable/Disable
			rainShieldDistance = builder
					.comment("Defines the range in which the Rain Shield stops rendering rain [default: 80]")
					.defineInRange("rainShieldDistance", 80, 1, Integer.MAX_VALUE);

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		RainShield.LOGGER.debug("Loaded Rain Shield's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		RainShield.LOGGER.warn("Rain Shield's config just got changed on the file system!");
	}
}
