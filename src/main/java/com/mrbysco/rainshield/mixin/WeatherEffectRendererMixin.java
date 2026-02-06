package com.mrbysco.rainshield.mixin;

import com.mrbysco.rainshield.client.RainHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {

	@Inject(method = "render(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/WeatherRenderState;Lnet/minecraft/client/renderer/state/LevelRenderState;)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;isEmpty()Z",
			shift = Shift.AFTER,
			ordinal = 0
	), cancellable = true)
	private void RainShield$render(MultiBufferSource bufferSource, Vec3 cameraPosition,
	                               WeatherRenderState renderState, LevelRenderState levelRenderState, CallbackInfo ci) {
		BlockPos pos = BlockPos.containing(cameraPosition);
		if (RainHelper.cancelRain(pos)) {
			ci.cancel();
		}
	}
}
