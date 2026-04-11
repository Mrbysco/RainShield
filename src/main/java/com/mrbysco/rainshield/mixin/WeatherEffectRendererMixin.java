package com.mrbysco.rainshield.mixin;

import com.mrbysco.rainshield.client.RainHelper;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
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

	@Inject(method = "render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;getTextureManager()Lnet/minecraft/client/renderer/texture/TextureManager;",
			shift = Shift.AFTER,
			ordinal = 0
	), cancellable = true)
	private void RainShield$render(Vec3 cameraPos, WeatherRenderState renderState, LevelRenderState levelRenderState, CallbackInfo ci) {
		BlockPos pos = BlockPos.containing(cameraPos);
		if (RainHelper.cancelRain(pos)) {
			ci.cancel();
		}
	}
}
