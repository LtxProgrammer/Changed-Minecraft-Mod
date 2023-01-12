package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.data.DeferredStateProvider;
import net.ltxprogrammer.changed.data.MixedStateProvider;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeFeatures.class)
public abstract class TreeFeaturesMixin {
    @Inject(method = "createStraightBlobTree", at = @At("HEAD"), cancellable = true)
    private static void createStraightBlobTree(Block log, Block leaf, int p_195149_, int p_195150_, int p_195151_, int p_195152_, CallbackInfoReturnable<TreeConfiguration.TreeConfigurationBuilder> callbackInfoReturnable) {
        if (log.defaultBlockState().is(Blocks.OAK_LOG) && leaf.defaultBlockState().is(Blocks.OAK_LEAVES)) {
            callbackInfoReturnable.setReturnValue(new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(log),
                    new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_),
                            MixedStateProvider.builder().add(BlockStateProvider.simple(leaf), 49).add(DeferredStateProvider.of(ChangedBlocks.ORANGE_TREE_LEAVES), 1).build(),
                    new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3),
                    new TwoLayersFeatureSize(1, 0, 1)));
        }
    }
}
