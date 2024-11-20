package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.LatexCoveredBlocks;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return super.useOn(context);

        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        LatexType thisType = LatexType.NEUTRAL;
        for (LatexType type : LatexType.values())
            if (this.getDefaultInstance().is(type.goo.get()))
                thisType = type;
        var event = new LatexCoveredBlocks.CoveringBlockEvent(thisType, state, context.getClickedPos(), context.getLevel());
        if (Changed.postModEvent(event))
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
