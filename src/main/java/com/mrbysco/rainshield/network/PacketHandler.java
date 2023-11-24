package com.mrbysco.rainshield.network;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.network.message.SyncShieldMapMessage;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(RainShield.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init() {
		CHANNEL.registerMessage(id++, SyncShieldMapMessage.class, SyncShieldMapMessage::encode, SyncShieldMapMessage::decode, SyncShieldMapMessage::handle);
	}
}
