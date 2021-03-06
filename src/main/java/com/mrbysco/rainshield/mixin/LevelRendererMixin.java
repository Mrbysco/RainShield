package com.mrbysco.rainshield.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	@Nullable
	private ClientLevel level;

	@Inject(method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
			shift = Shift.AFTER,
			ordinal = 0
	), cancellable = true
	)
	private void renderSnowAndRain(LightTexture lightTexture, float p_109705_, double x, double y, double z, CallbackInfo ci) {
		BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
		if (RainShieldData.cancelRain(level, pos)) {
			RenderSystem.disableBlend();
			lightTexture.turnOffLightLayer();
			ci.cancel();
		}
	}

	@Inject(method = "tickRain(Lnet/minecraft/client/Camera;)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;getPosition()Lnet/minecraft/world/phys/Vec3;",
			shift = Shift.AFTER,
			ordinal = 0
	), cancellable = true
	)
	private void tickRain(Camera camera, CallbackInfo ci) {
		BlockPos blockpos = new BlockPos(camera.getPosition());

		if (RainShieldData.cancelRain(level, blockpos)) {
			ci.cancel();
		}
	}
}
