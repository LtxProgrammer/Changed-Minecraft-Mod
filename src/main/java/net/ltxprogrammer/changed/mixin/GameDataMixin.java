package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.AbstractLatexGoo;
import net.ltxprogrammer.changed.util.StateHolderHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = GameData.class, remap = false)
public abstract class GameDataMixin {
    private static ResourceLocation r(String s) { return new ResourceLocation(s); }
    private static final Set<ResourceLocation> FIXED_BLOCKS = Set.of(
            r("bedrock"),
            r("integrateddynamics:cable")
    );

    @Inject(method = "freezeData", at = @At("HEAD"))
    private static void removeLatexCoveredStates(CallbackInfo callback) {
        HashSet<ResourceLocation> notCoverable = new HashSet<>(FIXED_BLOCKS);
        MinecraftForge.EVENT_BUS.post(new AbstractLatexGoo.GatherNonCoverableBlocksEvent(notCoverable));

        Registry.BLOCK.forEach(block -> {
            if (!block.getStateDefinition().getProperties().contains(AbstractLatexBlock.COVERED))
                return;

            if (notCoverable.contains(block.getRegistryName())) {
                var builder = new StateDefinition.Builder<Block, BlockState>(block);
                var oldDefault = block.defaultBlockState();
                block.getStateDefinition().getProperties().stream().filter(property -> property != AbstractLatexBlock.COVERED).forEach(builder::add);
                var newStateDefinition = builder.create(StateHolderHelper.FN_STATE_CREATION_BYPASS, BlockState::new);

                // Create new default state
                if (!(newStateDefinition.any() instanceof StateHolderHelper<?,?> newDefault))
                    throw new IllegalStateException("Mixin failed for StateHolderHelper");
                for (var property : newStateDefinition.getProperties())
                    newDefault = newDefault.setValueTypeless(property, oldDefault.getValue(property));

                // Update old state stuff
                block.stateDefinition = newStateDefinition;
                block.defaultBlockState = (BlockState) newDefault.getState();
            }
        });
    }
}
