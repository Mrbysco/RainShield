package com.mrbysco.rainshield.mixin;

import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mrbysco.rainshield.client.RainHelper;
import com.mrbysco.rainshield.client.ShieldData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V",
			at = @At(value = "HEAD"))
	private void RainShield$extractRenderState(ClientLevel level, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci) {
		if (renderState instanceof ShieldData shieldData) {
			shieldData.rainshield$setCameraPos(cameraPos);
		}
	}

	@Inject(method = "render(Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)V",
			at = @At(value = "HEAD"), cancellable = true)
	private void RainShield$render(WeatherRenderState renderState, RenderPass renderPass, RenderPipeline renderPipeline, CallbackInfo ci) {
		if (renderState instanceof ShieldData shieldData && RainHelper.cancelRain(BlockPos.containing(shieldData.rainshield$getCameraPos()))) {
			ci.cancel();
		}
	}
}
