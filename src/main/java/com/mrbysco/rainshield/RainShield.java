package com.mrbysco.rainshield;

import com.mrbysco.rainshield.client.RainShieldConfig;
import com.mrbysco.rainshield.handler.SyncHandler;
import com.mrbysco.rainshield.network.PacketHandler;
import com.mrbysco.rainshield.registry.RainShieldRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RainShield.MOD_ID)
public class RainShield {
    public static final String MOD_ID = "rainshield";
    public static final Logger LOGGER = LogManager.getLogger();

    public RainShield() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(Type.CLIENT, RainShieldConfig.commonSpec);
        eventBus.register(RainShieldConfig.class);

        eventBus.addListener(this::setup);

        RainShieldRegistry.BLOCKS.register(eventBus);
        RainShieldRegistry.ITEMS.register(eventBus);

        MinecraftForge.EVENT_BUS.register(new SyncHandler());
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
    }
}
