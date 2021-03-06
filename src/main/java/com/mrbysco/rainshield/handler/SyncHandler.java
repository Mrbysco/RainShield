package com.mrbysco.rainshield.handler;

import com.mrbysco.rainshield.network.PacketHandler;
import com.mrbysco.rainshield.network.message.SyncShieldMapMessage;
import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncHandler {
	@SubscribeEvent
	public void onLogin(PlayerLoggedInEvent event) {
		Player player = event.getPlayer();
		if (!player.level.isClientSide) {
			syncShieldMap((ServerPlayer) player);
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerLoggedOutEvent event) {
		Player player = event.getPlayer();
		if (player.level.isClientSide) {
			RainShieldData.rainShieldMap.clear();
		}
	}

	public static void syncShieldMap(ServerPlayer player) {
		RainShieldData rainShieldData = RainShieldData.get(player.getServer().getLevel(Level.OVERWORLD));
		CompoundTag tag = rainShieldData.save(new CompoundTag());
		PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncShieldMapMessage(tag));
	}
}
