package com.mrbysco.rainshield.mixin;

import com.mrbysco.rainshield.client.RainHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "tickWeatherEffects()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/RandomSource;createThreadLocalInstance(J)Lnet/minecraft/util/RandomSource;",
					shift = At.Shift.AFTER,
					ordinal = 0
			), cancellable = true)
	private void RainShield$tickWeatherEffects(CallbackInfo ci) {
		BlockPos cameraPosition = BlockPos.containing(this.minecraft.gameRenderer.mainCamera().position());
		if (RainHelper.cancelRain(cameraPosition)) {
			ci.cancel();
		}
	}
}
