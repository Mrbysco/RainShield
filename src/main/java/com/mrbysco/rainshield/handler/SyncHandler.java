package com.mrbysco.rainshield.handler;

import com.mrbysco.rainshield.network.payloads.SyncShieldMapPayload;
import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

public class SyncHandler {
	@SubscribeEvent
	public void onLogin(PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide) {
			syncShieldMap((ServerPlayer) player);
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		if (player.level().isClientSide) {
			RainShieldData.rainShieldMap.clear();
		}
	}

	public static void syncShieldMap(ServerPlayer player) {
		RainShieldData rainShieldData = RainShieldData.get(player.getServer().getLevel(Level.OVERWORLD));
		CompoundTag tag = rainShieldData.save(new CompoundTag());
		player.connection.send(new SyncShieldMapPayload(tag));
	}
}
