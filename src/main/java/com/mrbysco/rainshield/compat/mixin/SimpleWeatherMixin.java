package com.mrbysco.rainshield.compat.mixin;

import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "tv.soaryn.simpleweather.SimpleWeather$NeoBus", remap = false)
public interface SimpleWeatherMixin {
	@Inject(remap = false,
			at = @At("HEAD"),
			method = "shouldNotPrecipitateAt(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos$MutableBlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)Z",
			cancellable = true)
	private static void rainshield$shouldNotPrecipitateAt(ClientLevel level, BlockPos.MutableBlockPos pos,
	                                                      Biome.Precipitation precipitation, CallbackInfoReturnable<Boolean> cir) {
		if (RainShieldData.cancelRain(level, pos)) {
			cir.setReturnValue(true);
		}
	}
}
