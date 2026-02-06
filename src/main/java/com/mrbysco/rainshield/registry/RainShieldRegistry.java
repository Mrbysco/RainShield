package com.mrbysco.rainshield.registry;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.block.RainShieldBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RainShieldRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RainShield.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RainShield.MOD_ID);

	@SuppressWarnings("deprecation")
	public static final DeferredBlock<RainShieldBlock> RAIN_SHIELD = BLOCKS.registerBlock("rain_shield",
			RainShieldBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noCollision().strength(0.8F)
					.forceSolidOff().instabreak().sound(SoundType.METAL).noOcclusion());

	public static final DeferredItem<BlockItem> RAIN_SHIELD_ITEM = ITEMS.registerSimpleBlockItem(RAIN_SHIELD);
}
