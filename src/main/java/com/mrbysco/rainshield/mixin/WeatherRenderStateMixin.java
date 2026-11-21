package com.mrbysco.rainshield.mixin;

import com.mrbysco.rainshield.client.ShieldData;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WeatherRenderState.class)
public class WeatherRenderStateMixin implements ShieldData {
	@Unique
	private Vec3 rainshield$cameraPos;

	@Override
	public Vec3 rainshield$getCameraPos() {
		return rainshield$cameraPos;
	}

	@Override
	public void rainshield$setCameraPos(Vec3 pos) {
		rainshield$cameraPos = pos;
	}
}
