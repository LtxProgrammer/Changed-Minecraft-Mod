package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.StateHolderHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public class AbstractLatexItem extends ItemNameBlockItem {
    private final LatexType type;

    public AbstractLatexItem(LatexType type) {
        // TODO make better
        super(type == LatexType.DARK_LATEX ? ChangedBlocks.DARK_LATEX_WALL_SPLOTCH.get() : ChangedBlocks.WHITE_LATEX_WALL_SPLOTCH.get(),
                new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS).food(Foods.DRIED_KELP));
        this.type = type;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        var variants = TransfurVariant.getPublicTransfurVariants().filter(variant -> variant.type == type);
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), (player, variant) -> {
            if (variant.getLatexType().isHostileTo(type))
                player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
        });
        final var variant = Util.getRandom(variants.toList(), level.random);
        ProcessTransfur.progressTransfur(entity, 11.0f, variant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
        return super.finishUsingItem(itemStack, level, entity);
    }

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
    public static synchronized void removeLatexCoveredStates() {
        if (!removalCompleted) {
            HashSet<RegistryElementPredicate<Block>> notCoverable = new HashSet<>();
            MinecraftForge.EVENT_BUS.post(new AbstractLatexItem.GatherNonCoverableBlocksEvent(notCoverable));

            ForgeRegistries.BLOCKS.forEach(block -> {
                if (!block.getStateDefinition().getProperties().contains(AbstractLatexBlock.COVERED))
                    return;

                if (notCoverable.stream().anyMatch(pred -> pred.test(block))) {
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

            removalCompleted = true;
        }
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

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return super.useOn(context);

        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        LatexType thisType = LatexType.NEUTRAL;
        for (LatexType type : LatexType.values())
            if (this.getDefaultInstance().is(type.goo.get()))
                thisType = type;
        var event = new CoveringBlockEvent(thisType, state, context.getClickedPos(), context.getLevel());
        if (MinecraftForge.EVENT_BUS.post(event))
            return InteractionResult.FAIL;
        if (event.originalState == event.plannedState)
            return InteractionResult.FAIL;
        if (!Changed.config.server.canBlockBeCovered(event.plannedState.getBlock()))
            return InteractionResult.FAIL;

        event.level.setBlockAndUpdate(event.blockPos, event.plannedState);
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    public LatexType getLatexType() {
        return type;
    }
}
