package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.ltxprogrammer.changed.block.TextMenuProvider;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextBlockEntity extends BlockEntity implements MenuProvider, TextEnterable {
    public String text = "";

    public TextBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.TEXT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Text", text);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Text"))
            text = tag.getString("Text");
    }

    private static final Component NOTE = new TranslatableComponent("container.changed.note");
    @Override
    public Component getDisplayName() {
        return NOTE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        if (this.getBlockState().getBlock() instanceof TextMenuProvider provider)
            return provider.createMenu(this.getBlockState(), player.level, this.worldPosition, id, inv, player);
        return null;
    }

    @Override
    public void setText(String text) {
        if (this.text.isEmpty())
            this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public BlockEntity getSelf() {
        return this;
    }
}
