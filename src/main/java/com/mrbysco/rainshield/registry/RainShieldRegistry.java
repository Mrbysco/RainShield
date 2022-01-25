package com.mrbysco.rainshield.registry;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.block.RainShieldBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RainShieldRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RainShield.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RainShield.MOD_ID);

	public static final RegistryObject<Block> RAIN_SHIELD = BLOCKS.register("rain_shield", () ->
			new RainShieldBlock(Block.Properties.of(Material.DECORATION).strength(0.8F).instabreak().sound(SoundType.METAL).noOcclusion()));

	public static final RegistryObject<Item> RAIN_SHIELD_ITEM = ITEMS.register("rain_shield", () -> new BlockItem(RAIN_SHIELD.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
