package com.mrbysco.rainshield;

import com.mojang.logging.LogUtils;
import com.mrbysco.rainshield.client.RainShieldConfig;
import com.mrbysco.rainshield.handler.SyncHandler;
import com.mrbysco.rainshield.network.PacketHandler;
import com.mrbysco.rainshield.registry.RainShieldRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(RainShield.MOD_ID)
public class RainShield {
	public static final String MOD_ID = "rainshield";
	public static final Logger LOGGER = LogUtils.getLogger();

	public RainShield(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(Type.CLIENT, RainShieldConfig.commonSpec);
		eventBus.register(RainShieldConfig.class);

		eventBus.addListener(this::setup);
		eventBus.addListener(this::addTabContents);

		RainShieldRegistry.BLOCKS.register(eventBus);
		RainShieldRegistry.ITEMS.register(eventBus);

		NeoForge.EVENT_BUS.register(new SyncHandler());
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	private void addTabContents(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
			event.accept(RainShieldRegistry.RAIN_SHIELD_ITEM.get());
		}
	}
}
