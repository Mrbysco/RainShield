package com.mrbysco.rainshield.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.registry.RainShieldRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RainShieldDataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new Loots(generator));
			generator.addProvider(event.includeServer(), new Recipes(generator));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeClient(), new Language(generator));
			generator.addProvider(event.includeClient(), new BlockModels(generator, helper));
			generator.addProvider(event.includeClient(), new ItemModels(generator, helper));
			generator.addProvider(event.includeClient(), new BlockStates(generator, helper));
		}
	}

	private static class Loots extends LootTableProvider {
		public Loots(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
			return ImmutableList.of(
					Pair.of(GeOreBlockTables::new, LootContextParamSets.BLOCK)
			);
		}

		public static class GeOreBlockTables extends BlockLoot {
			@Override
			protected void addTables() {
				dropSelf(RainShieldRegistry.RAIN_SHIELD.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) RainShieldRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
		}
	}

	public static class Recipes extends RecipeProvider {
		public Recipes(DataGenerator generator) {
			super(generator);
		}

		@Override
		protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
			ShapedRecipeBuilder.shaped(RainShieldRegistry.RAIN_SHIELD.get())
					.define('F', Items.FLINT)
					.define('B', Tags.Items.RODS_BLAZE)
					.define('N', Tags.Items.NETHERRACK)
					.pattern(" F ").pattern(" B ").pattern("NNN").unlockedBy("has_blaze_rod",
							has(Tags.Items.RODS_BLAZE)).save(recipeConsumer);
		}
	}

	private static class Language extends LanguageProvider {
		public Language(DataGenerator gen) {
			super(gen, RainShield.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addBlock(RainShieldRegistry.RAIN_SHIELD, "Rain Shield");
		}
	}

	private static class BlockStates extends BlockStateProvider {
		public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, RainShield.MOD_ID, helper);
		}

		@Override
		protected void registerStatesAndModels() {
			makeRod(RainShieldRegistry.RAIN_SHIELD.get());
		}

		private void makeRod(Block block) {
			ModelFile model = models().getExistingFile(modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block).getPath()));
			getVariantBuilder(block)
					.partialState().with(BlockStateProperties.FACING, Direction.DOWN)
					.modelForState().modelFile(model).rotationX(180).addModel()
					.partialState().with(BlockStateProperties.FACING, Direction.EAST)
					.modelForState().modelFile(model).rotationX(90).rotationY(90).addModel()
					.partialState().with(BlockStateProperties.FACING, Direction.NORTH)
					.modelForState().modelFile(model).rotationX(90).addModel()
					.partialState().with(BlockStateProperties.FACING, Direction.SOUTH)
					.modelForState().modelFile(model).rotationX(90).rotationY(180).addModel()
					.partialState().with(BlockStateProperties.FACING, Direction.UP)
					.modelForState().modelFile(model).addModel()
					.partialState().with(BlockStateProperties.FACING, Direction.WEST)
					.modelForState().modelFile(model).rotationX(90).rotationY(270).addModel();
		}
	}

	private static class BlockModels extends BlockModelProvider {
		public BlockModels(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, RainShield.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			makeRod(RainShieldRegistry.RAIN_SHIELD.get());
		}

		private void makeRod(Block block) {
			ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
			withExistingParent(location.getPath(), modLoc("block/rod"))
					.texture("particle", "block/" + location.getPath())
					.texture("rod", "block/" + location.getPath());
		}
	}

	private static class ItemModels extends ItemModelProvider {
		public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, RainShield.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			ResourceLocation location = ForgeRegistries.ITEMS.getKey(RainShieldRegistry.RAIN_SHIELD_ITEM.get());
			withExistingParent(location.getPath(), modLoc("block/" + location.getPath()));
		}
	}
}