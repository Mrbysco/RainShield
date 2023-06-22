package com.mrbysco.rainshield.datagen;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.registry.RainShieldRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
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
import java.util.Set;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RainShieldDataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new Loots(packOutput));
			generator.addProvider(event.includeServer(), new Recipes(packOutput));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeClient(), new Language(packOutput));
			generator.addProvider(event.includeClient(), new BlockModels(packOutput, helper));
			generator.addProvider(event.includeClient(), new ItemModels(packOutput, helper));
			generator.addProvider(event.includeClient(), new BlockStates(packOutput, helper));
		}
	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput) {
			super(packOutput, Set.of(), null);
		}

		public List<SubProviderEntry> getTables() {
			return List.of(
					new SubProviderEntry(RainShieldBlockTables::new, LootContextParamSets.BLOCK)
			);
		}

		public static class RainShieldBlockTables extends BlockLootSubProvider {

			protected RainShieldBlockTables() {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags());
			}

			@Override
			protected void generate() {
				dropSelf(RainShieldRegistry.RAIN_SHIELD.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) RainShieldRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> table.validate(validationContext));
		}
	}

	public static class Recipes extends RecipeProvider {
		public Recipes(PackOutput packOutput) {
			super(packOutput);
		}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RainShieldRegistry.RAIN_SHIELD.get())
					.define('F', Items.FLINT)
					.define('B', Tags.Items.RODS_BLAZE)
					.define('N', Tags.Items.NETHERRACK)
					.pattern(" F ").pattern(" B ").pattern("NNN").unlockedBy("has_blaze_rod",
							has(Tags.Items.RODS_BLAZE)).save(consumer);
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, RainShield.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addBlock(RainShieldRegistry.RAIN_SHIELD, "Rain Shield");
		}
	}

	private static class BlockStates extends BlockStateProvider {
		public BlockStates(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, RainShield.MOD_ID, helper);
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
		public BlockModels(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, RainShield.MOD_ID, helper);
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
		public ItemModels(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, RainShield.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			ResourceLocation location = ForgeRegistries.ITEMS.getKey(RainShieldRegistry.RAIN_SHIELD_ITEM.get());
			withExistingParent(location.getPath(), modLoc("block/" + location.getPath()));
		}
	}
}