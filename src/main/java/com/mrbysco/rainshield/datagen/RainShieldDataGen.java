package com.mrbysco.rainshield.datagen;

import com.mrbysco.rainshield.RainShield;
import com.mrbysco.rainshield.registry.RainShieldRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class RainShieldDataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new Loots(packOutput, lookupProvider));
		generator.addProvider(true, new Recipes.Runner(packOutput, lookupProvider));

		generator.addProvider(true, new Language(packOutput));
		generator.addProvider(true, new Models(packOutput));

	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(packOutput, Set.of(), null, lookupProvider);
		}

		public List<SubProviderEntry> getTables() {
			return List.of(
					new SubProviderEntry(RainShieldBlockTables::new, LootContextParamSets.BLOCK)
			);
		}

		public static class RainShieldBlockTables extends BlockLootSubProvider {

			protected RainShieldBlockTables(HolderLookup.Provider provider) {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
			}

			@Override
			protected void generate() {
				dropSelf(RainShieldRegistry.RAIN_SHIELD.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) RainShieldRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
			}
		}
	}

	public static class Recipes extends RecipeProvider {
		public Recipes(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
		}

		@Override
		protected void buildRecipes() {
			shaped(RecipeCategory.REDSTONE, RainShieldRegistry.RAIN_SHIELD.get())
					.define('F', Items.FLINT)
					.define('B', Tags.Items.RODS_BLAZE)
					.define('N', Tags.Items.NETHERRACKS)
					.pattern(" F ").pattern(" B ").pattern("NNN").unlockedBy("has_blaze_rod",
							has(Tags.Items.RODS_BLAZE)).save(output);
		}

		public static class Runner extends RecipeProvider.Runner {
			public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
				super(output, completableFuture);
			}

			@Override
			protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
				return new Recipes(provider, recipeOutput);
			}

			@Override
			public String getName() {
				return "Rain Shield Recipes";
			}
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, RainShield.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addBlock(RainShieldRegistry.RAIN_SHIELD, "Rain Shield");

			add("rainshield.networking.sync_shields.failed", "Failed to sync rain shield data: %s");

			addConfig("client", "Client", "Client Settings");
			addConfig("rainShieldDistance", "Rain Shield Distance", "Defines the range in which the Rain Shield stops rendering rain");
		}

		/**
		 * Add the translation for a config entry
		 *
		 * @param path        The path of the config entry
		 * @param name        The name of the config entry
		 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
		 */
		private void addConfig(String path, String name, @Nullable String description) {
			this.add(RainShield.MOD_ID + ".configuration." + path, name);
			if (description != null && !description.isEmpty())
				this.add(RainShield.MOD_ID + ".configuration." + path + ".tooltip", description);
		}
	}

	private static class Models extends ModelProvider {
		public static final TextureSlot ROD_SLOT = TextureSlot.create("rod");
		public static final ModelTemplate ROD = ModelTemplates.create("rainshield:rod", ROD_SLOT);

		public Models(PackOutput packOutput) {
			super(packOutput, RainShield.MOD_ID);
		}

		@Override
		protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
			makeRod(blockModels, RainShieldRegistry.RAIN_SHIELD);
		}

		private void makeRod(BlockModelGenerators blockModels, DeferredBlock<? extends Block> deferredBlock) {
			Identifier model = ROD.create(deferredBlock.get(),
					TextureMapping.singleSlot(ROD_SLOT, new Material(deferredBlock.getId().withPrefix("block/"))),
					blockModels.modelOutput);

			blockModels.blockStateOutput
					.accept(
							MultiVariantGenerator.dispatch(deferredBlock.get(),
											BlockModelGenerators.plainVariant(model))
									.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING)
					);
		}
	}
}