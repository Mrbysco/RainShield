package com.mrbysco.rainshield;

import com.mojang.logging.LogUtils;
import com.mrbysco.rainshield.client.RainShieldConfig;
import com.mrbysco.rainshield.handler.SyncHandler;
import com.mrbysco.rainshield.network.PacketHandler;
import com.mrbysco.rainshield.registry.RainShieldRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RainShield.MOD_ID)
public class RainShield {
	public static final String MOD_ID = "rainshield";
	public static final Logger LOGGER = LogUtils.getLogger();

	public RainShield() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(Type.CLIENT, RainShieldConfig.commonSpec);
		eventBus.register(RainShieldConfig.class);

		eventBus.addListener(this::setup);
		eventBus.addListener(this::addTabContents);

		RainShieldRegistry.BLOCKS.register(eventBus);
		RainShieldRegistry.ITEMS.register(eventBus);

		MinecraftForge.EVENT_BUS.register(new SyncHandler());
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
