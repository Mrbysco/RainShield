package com.mrbysco.rainshield.mixin;

import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {

	@Inject(method = "render(Lnet/minecraft/world/level/Level;Lnet/minecraft/client/renderer/MultiBufferSource;IFLnet/minecraft/world/phys/Vec3;)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;useFancyGraphics()Z",
			shift = Shift.AFTER,
			ordinal = 0
	), cancellable = true)
	private void RainShield$render(Level level, MultiBufferSource bufferSource, int ticks, float partialTick, Vec3 cameraPosition, CallbackInfo ci, float f) {
		BlockPos pos = BlockPos.containing(cameraPosition);
		if (RainShieldData.cancelRain(level, pos)) {
			ci.cancel();
		}
	}
}
