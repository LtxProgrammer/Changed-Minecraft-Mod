package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class CompactDisc extends ItemNameBlockItem {
    public CompactDisc() {
        super(ChangedBlocks.CD_STACK.get(), new Properties().stacksTo(16));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> hoverText, TooltipFlag tooltipFlag) {
        var tag = stack.getTag();
        if (tag != null) {
            String s = DiscData.getName(tag.getCompound("fs"));
            if (!StringUtil.isNullOrEmpty(s)) {
                hoverText.add((Component.translatable("text.changed.compact_disc.title", s)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public boolean isFoil(ItemStack stack) {
        var tag = stack.getTag();
        return tag != null && !tag.getCompound("fs").isEmpty();
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState) {
        if (super.placeBlock(blockPlaceContext, blockState)) {
            blockPlaceContext.getLevel().getBlockEntity(blockPlaceContext.getClickedPos(), ChangedBlockEntities.CD_STACK.get()).ifPresent(blockEntity -> {
                if (!blockPlaceContext.getLevel().isClientSide)
                    blockEntity.push(blockPlaceContext.getItemInHand());
            });

            return true;
        }

        return false;
    }
}
