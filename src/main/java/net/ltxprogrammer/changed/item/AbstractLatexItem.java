package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ai.ImmediateTransfurDecision;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class AbstractLatexItem extends ItemNameBlockItem {
    private final Supplier<? extends SpreadingLatexType> type;

    public AbstractLatexItem(Block block, Supplier<? extends SpreadingLatexType> type) {
        super(block, new Properties().food(Foods.DRIED_KELP));
        this.type = type;
    }

    protected ImmediateTransfurDecision<?> makeAssimilationDecision(LivingEntity target) {
        final var variant = type.get().getTransfurVariant(TransfurCause.ATE_LATEX, target.getRandom());
        return ImmediateTransfurDecision.unsafe(variant, TransfurCause.LATEX_PUDDLE);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), (player, variant) -> {
            if (variant.getLatexType().isHostileTo(type.get()))
                player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
        });
        ProcessTransfur.transfur(entity, this.makeAssimilationDecision(entity));
        return super.finishUsingItem(itemStack, level, entity);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return super.useOn(context);

        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        if (clickedState.is(ChangedTags.Blocks.DENY_LATEX_COVER))
            return InteractionResult.FAIL;

        BlockPos positionToCover = clickedState.isFaceSturdy(context.getLevel(), context.getClickedPos(), context.getClickedFace(), SupportType.FULL) ?
                context.getClickedPos().relative(context.getClickedFace()) : context.getClickedPos();

        BlockState originalState = context.getLevel().getBlockState(positionToCover);
        if (SpreadingLatexType.canExistOnSurface(context.getLevel(), positionToCover, originalState, positionToCover, originalState, context.getClickedFace()))
            return InteractionResult.FAIL;

        LatexCoverState originalCover = LatexCoverState.getAt(context.getLevel(), positionToCover);
        final var spreadingLatexType = type.get();

        var event = new SpreadingLatexType.CoveringBlockEvent(spreadingLatexType, originalState, originalState,
                spreadingLatexType.spreadState(context.getLevel(), positionToCover, spreadingLatexType.sourceCoverState()), positionToCover, context.getLevel());
        spreadingLatexType.defaultCoverBehavior(event);
        if (Changed.postModEvent(event))
            return InteractionResult.FAIL;
        if (event.originalState == event.getPlannedState() && event.plannedCoverState == originalCover)
            return InteractionResult.FAIL;
        // TODO revisit config
        /*if (!Changed.config.server.canBlockBeCovered(event.plannedState.getBlock()))
            return InteractionResult.FAIL;*/

        context.getLevel().setBlockAndUpdate(event.blockPos, event.getPlannedState());
        LatexCoverState.setAtAndUpdate(context.getLevel(), event.blockPos, event.plannedCoverState);

        final var soundType = event.plannedCoverState.getSoundType(context.getLevel(), event.blockPos, context.getPlayer());
        if (soundType != null)
            context.getLevel().playSound(context.getPlayer(), event.blockPos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

        event.getPostProcess().accept(context.getLevel(), positionToCover);

        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    public LatexType getLatexType() {
        return type.get();
    }
}
