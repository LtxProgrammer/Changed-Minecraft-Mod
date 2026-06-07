package net.ltxprogrammer.changed.datagen;

import net.ltxprogrammer.changed.datagen.ability.AbilityNodesProvider;
import net.ltxprogrammer.changed.datagen.ability.AbilityTreeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GatherData {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        // All the Variables upwards of this comment may become needed by a specific kind of datagen

        generator.addProvider(true, new AccessoryEntityProvider(generator));
        generator.addProvider(true, new TFTagsProvider(packOutput, lookupProvider, helper));

        CompletableFuture<HolderLookup.Provider> lookup0 =
                generator.addProvider(event.includeServer(), new DatapackEntriesProvider(packOutput, lookupProvider)).getRegistryProvider();

        generator.addProvider(event.includeServer(), new DamageTypeTagProvider(packOutput, lookup0, helper));
        generator.addProvider(event.includeServer(), new AbilityNodesProvider(packOutput));
        generator.addProvider(event.includeServer(), new AbilityTreeProvider(packOutput));

//        BlockTagsProvider blocks = new BlockTagsProvider(packOutput, lookupProvider, helper);
//        generator.addProvider(true, blocks);
//        generator.addProvider(true, new ItemTagsProvider(generator, lookupProvider, blocks.contentsGetter(), existingFileHelper));
//        generator.addProvider(true, new FluidTagsProvider(packOutput, lookupProvider, helper));
//
//        generator.addProvider(true, new EntityTypeTagsProvider(packOutput, lookupProvider, helper));
//        generator.addProvider(true, new CAPaintingVariantTagsProvider(packOutput, lookupProvider, helper));
//
//        generator.addProvider(event.includeServer(), new BiomeTagProvider(packOutput, lookup0, helper));
//
//        generator.addProvider(true, new RecipeProvider(packOutput));
//
//        generator.addProvider(true, new LootTableProvider(packOutput));
//
//        generator.addProvider(true, new BlockStateProvider(packOutput, helper));
//        generator.addProvider(true, new ItemModelProvider(packOutput, helper));
//        generator.addProvider(new AdvancementProvider(generator, helper));

    }
}
