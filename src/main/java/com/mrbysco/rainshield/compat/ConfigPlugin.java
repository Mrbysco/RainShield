package com.mrbysco.rainshield.compat;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ConfigPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
		if (isModLoaded("simple_weather")) {
			System.out.println("[RainShield] Simple Weather is loaded, applying compat");
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return isModLoaded("simple_weather");
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		// noop
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		// noop
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		// noop
	}

	private static boolean isModLoaded(String modId) {
		if (ModList.get() == null) {
			return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
		}
		return ModList.get().isLoaded(modId);
	}
}
