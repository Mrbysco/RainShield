package com.mrbysco.rainshield.network;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.network.handler.ClientPayloadHandler;
import com.mrbysco.rainshield.network.payloads.SyncShieldMapPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(RainShield.MOD_ID);

		registrar.playToClient(SyncShieldMapPayload.ID, SyncShieldMapPayload.CODEC, ClientPayloadHandler.getInstance()::handleData);
	}
}
