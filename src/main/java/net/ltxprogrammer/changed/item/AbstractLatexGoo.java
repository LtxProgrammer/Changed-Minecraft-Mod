package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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

    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getProperties().contains(COVERED) && state.getValue(COVERED) == LatexType.NEUTRAL) {
            for (LatexType type : LatexType.values())
                if (this.getDefaultInstance().is(type.goo.get())) {
                    context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(COVERED, type));
                    context.getItemInHand().shrink(1);
                    return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
                }

            return InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    public LatexType getLatexType() {
        return type;
    }
}
