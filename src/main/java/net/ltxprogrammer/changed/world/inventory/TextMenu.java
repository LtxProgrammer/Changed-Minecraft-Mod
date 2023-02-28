package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class TextMenu extends AbstractContainerMenu implements UpdateableMenu {
    protected final @Nullable BlockPos blockPos;
    protected final @NotNull BlockState blockState;
    protected final @Nullable TextEnterable textMenuBlockEntity;
    protected final Level level;
    protected final Player player;
    public String textCopy = "";
    public String textCopyLastReceived = "";

    public TextMenu(MenuType<?> type, int id, Inventory inventory, BlockPos pos, BlockState state, TextEnterable textMenuBlockEntity) {
        super(type, id);
        this.level = inventory.player.level;
        this.player = inventory.player;
        this.blockPos = pos;
        this.blockState = state;
        this.textMenuBlockEntity = textMenuBlockEntity;
        this.textCopy = textMenuBlockEntity.getText();

        CompoundTag data = new CompoundTag();
        data.putString("Text", textCopy);
        this.setDirty(data);
    }

    public TextMenu(MenuType<?> type, int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(type, id);
        this.level = inventory.player.level;
        this.player = inventory.player;
        this.blockPos = null;
        this.blockState = Blocks.AIR.defaultBlockState();
        this.textMenuBlockEntity = null;
    }

    @Override
    public boolean stillValid(Player player) {
        return textMenuBlockEntity != null && !textMenuBlockEntity.getSelf().isRemoved() && !player.isSpectator();
    }

    @Override
    public int getId() {
        return containerId;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && textMenuBlockEntity != null) {
            if (payload.contains("Text")) {
                textCopy = payload.getString("Text");
                textCopyLastReceived = textCopy;
                textMenuBlockEntity.setText(textCopy);
            } else {
                CompoundTag data = new CompoundTag();
                data.putString("Text", textCopy);
                this.setDirty(data);
            }
        } else if (receiver.isClient()) {
            textCopy = payload.getString("Text");
            textCopyLastReceived = textCopy;
        }
    }

    public void setText(String text) {
        if (!level.isClientSide && textMenuBlockEntity != null)
            textMenuBlockEntity.setText(text);
        else {
            textCopy = text;
            CompoundTag tag = new CompoundTag();
            tag.putString("Text", text);
            this.setDirty(tag);
        }
    }
}
