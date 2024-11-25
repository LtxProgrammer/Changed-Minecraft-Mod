package net.ltxprogrammer.changed.process;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

/**
 * Facilitates editing block states to have/remove the covered_with property.
 */
public abstract class LatexCoveredBlocks {
    public static class GatherNonCoverableBlocksEvent extends Event {
        private static RegistryElementPredicate<Block> r(String s) { return RegistryElementPredicate.parseString(ForgeRegistries.BLOCKS, s); }
        private static final Set<RegistryElementPredicate<Block>> FIXED_BLOCKS = Set.of(
                r("bedrock"),
                r("integrateddynamics:cable"),
                r("dragonsurvival:oak_dragon_door"),
                r("dragonsurvival:spruce_dragon_door"),
                r("dragonsurvival:acacia_dragon_door"),
                r("dragonsurvival:birch_dragon_door"),
                r("dragonsurvival:jungle_dragon_door"),
                r("dragonsurvival:dark_oak_dragon_door"),
                r("dragonsurvival:warped_dragon_door"),
                r("dragonsurvival:crimson_dragon_door"),
                r("dragonsurvival:cave_dragon_door"),
                r("dragonsurvival:forest_dragon_door"),
                r("dragonsurvival:sea_dragon_door"),
                r("dragonsurvival:iron_dragon_door"),
                r("@chiselsandbits")
        );

        private final HashSet<RegistryElementPredicate<Block>> set;

        public GatherNonCoverableBlocksEvent(HashSet<RegistryElementPredicate<Block>> set) {
            this.set = set;
            this.set.addAll(FIXED_BLOCKS);
        }

        public void addBlock(Block block) {
            set.add(RegistryElementPredicate.forID(ForgeRegistries.BLOCKS, block.getRegistryName()));
        }

        public void addBlock(Supplier<? extends Block> block) {
            set.add(RegistryElementPredicate.forID(ForgeRegistries.BLOCKS, block.get().getRegistryName()));
        }

        public void addBlock(ResourceLocation registryName) {
            set.add(RegistryElementPredicate.forID(ForgeRegistries.BLOCKS, registryName));
        }

        public void addNamespace(String namespace) {
            set.add(RegistryElementPredicate.forNamespace(ForgeRegistries.BLOCKS, namespace));
        }
    }

    private static boolean removalCompleted = false;
    private static boolean removingStates = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static synchronized void removeLatexCoveredStates() {
        if (!removalCompleted) {
            removingStates = true;

            HashSet<RegistryElementPredicate<Block>> notCoverable = new HashSet<>();
            Changed.postModEvent(new LatexCoveredBlocks.GatherNonCoverableBlocksEvent(notCoverable));

            ForgeRegistries.BLOCKS.forEach(block -> {
                if (!block.getStateDefinition().getProperties().contains(AbstractLatexBlock.COVERED))
                    return;

                if (notCoverable.stream().anyMatch(pred -> pred.test(block))) {
                    var builder = new StateDefinition.Builder<Block, BlockState>(block);
                    var oldDefault = block.defaultBlockState();
                    block.getStateDefinition().getProperties().stream().filter(property -> property != AbstractLatexBlock.COVERED).forEach(builder::add);
                    var newStateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
                    var newDefault = newStateDefinition.any();

                    // Create new default state
                    for (Property property : newStateDefinition.getProperties())
                        newDefault = newDefault.setValue(property, oldDefault.getValue(property));

                    // Update old state stuff
                    block.stateDefinition = newStateDefinition;
                    block.defaultBlockState = newDefault;
                }
            });

            removalCompleted = true;
            removingStates = false;
        }
    }

    public static boolean isRemovingStates() {
        return removingStates;
    }

    public static class CoveringBlockEvent extends Event {
        public final LatexType latexType;
        public final BlockPos blockPos;
        public final BlockState originalState;
        public final Level level;
        public BlockState plannedState;

        public CoveringBlockEvent(LatexType latexType, BlockState originalState, BlockPos blockPos, Level level) {
            this.latexType = latexType;
            this.blockPos = blockPos;
            this.originalState = originalState;
            this.level = level;
            if (originalState.getProperties().contains(COVERED) && originalState.getValue(COVERED) == LatexType.NEUTRAL)
                plannedState = originalState.setValue(COVERED, latexType);
            else
                plannedState = originalState;

            if (originalState.is(Blocks.GRASS_BLOCK))
                plannedState = Blocks.DIRT.defaultBlockState().setValue(COVERED, latexType);
            else if (originalState.is(Blocks.GRASS))
                plannedState = Blocks.DEAD_BUSH.defaultBlockState().setValue(COVERED, latexType);
            else if (originalState.is(BlockTags.SMALL_FLOWERS))
                plannedState = Blocks.DEAD_BUSH.defaultBlockState().setValue(COVERED, latexType);
            else if (originalState.is(BlockTags.SAPLINGS))
                plannedState = Blocks.DEAD_BUSH.defaultBlockState().setValue(COVERED, latexType);
            else if (originalState.is(Blocks.FERN))
                plannedState = Blocks.DEAD_BUSH.defaultBlockState().setValue(COVERED, latexType);
        }

        @Override public boolean isCancelable() {
            return true;
        }
        public void setPlannedState(BlockState blockState) {
            this.plannedState = blockState;
        }
    }
}
