package com.mrbysco.rainshield.network;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.network.handler.ClientPayloadHandler;
import com.mrbysco.rainshield.network.payloads.SyncShieldMapPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {
	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(RainShield.MOD_ID);

		registrar.play(SyncShieldMapPayload.ID, SyncShieldMapPayload::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleData));
	}
}
