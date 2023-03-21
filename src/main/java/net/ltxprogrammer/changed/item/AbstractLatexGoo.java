package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public class AbstractLatexGoo extends Item {
    private final LatexType type;

    public AbstractLatexGoo(LatexType type) {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS).food(Foods.DRIED_KELP));
        this.type = type;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        var variants = LatexVariant.VARIANTS_BY_TYPE.get(type);
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(entity), (player, variant) -> {
            if (variant.getLatexType().isHostileTo(type))
                player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
        });
        ProcessTransfur.progressTransfur(entity, 10000, variants.get(level.getRandom().nextInt(variants.size())));
        return super.finishUsingItem(itemStack, level, entity);
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
        }

        @Override public boolean isCancelable() {
            return true;
        }
        public void setPlannedState(BlockState blockState) {
            this.plannedState = blockState;
        }
    }

    public InteractionResult useOn(UseOnContext context) {
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
        event.level.setBlockAndUpdate(event.blockPos, event.plannedState);
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    public LatexType getLatexType() {
        return type;
    }
}
