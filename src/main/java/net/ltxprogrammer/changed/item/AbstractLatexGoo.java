package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public class AbstractLatexGoo extends AbstractLatexItem {
    private final LatexType type;

    public AbstractLatexGoo(LatexType type) {
        this.type = type;
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
