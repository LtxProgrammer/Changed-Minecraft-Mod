package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.block.Computer;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FloppyDisk extends Item {
    public static final String TAG_TITLE = "title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_DATA = "data";
    public static final String TAG_TRANSLATE = "translate";

    public FloppyDisk() {
        super(new Item.Properties().stacksTo(1).tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    public @NotNull Component getName(ItemStack p_43480_) {
        CompoundTag compoundtag = p_43480_.getTag();
        if (compoundtag != null) {
            String s = compoundtag.getString(TAG_TITLE);
            if (!StringUtil.isNullOrEmpty(s)) {
                if (TagUtil.getBooleanOrDefault(compoundtag, TAG_TRANSLATE, false))
                    return new TranslatableComponent(s);
                else
                    return new TextComponent(s);
            }
        }

        return super.getName(p_43480_);
    }

    public void appendHoverText(ItemStack p_43457_, @Nullable Level p_43458_, List<Component> p_43459_, TooltipFlag p_43460_) {
        if (p_43457_.hasTag()) {
            CompoundTag compoundtag = p_43457_.getTag();
            String s = compoundtag.getString(TAG_AUTHOR);
            if (!StringUtil.isNullOrEmpty(s)) {
                p_43459_.add((new TranslatableComponent("book.byAuthor", s)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public InteractionResult useOn(UseOnContext p_43466_) {
        Level level = p_43466_.getLevel();
        BlockPos blockpos = p_43466_.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(ChangedBlocks.COMPUTER.get())) {
            return Computer.tryUseDisk(p_43466_.getPlayer(), level, blockpos, blockstate, p_43466_.getItemInHand()) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public boolean isFoil(ItemStack p_43476_) {
        return p_43476_.getTag() != null;
    }
}
