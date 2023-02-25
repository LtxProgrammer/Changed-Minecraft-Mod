package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.entity.KeypadBlockEntity;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;

public class KeypadMenu extends AbstractContainerMenu implements UpdateableMenu {
    private final BlockPos blockPos;
    private final BlockState blockState;
    private final KeypadBlockEntity blockEntity;
    private final Level level;
    private final Player player;

    public KeypadMenu(int id, Inventory inventory, BlockPos pos, BlockState state, KeypadBlockEntity blockEntity) {
        super(ChangedMenus.KEYPAD, id);
        this.level = inventory.player.level;
        this.player = inventory.player;
        this.blockPos = pos;
        this.blockState = state;
        this.blockEntity = blockEntity;
    }

    public KeypadMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.KEYPAD, id);
        this.level = inventory.player.level;
        this.player = inventory.player;
        this.blockPos = null;
        this.blockState = Blocks.AIR.defaultBlockState();
        this.blockEntity = null;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && !blockEntity.isRemoved();
    }

    public void useCode(List<Byte> attemptedCode) {
        if (this.level.isClientSide) {
            CompoundTag tag = new CompoundTag();
            tag.putByteArray("Code", attemptedCode);
            this.setDirty(tag);
        } else {
            if (blockEntity == null)
                return;
            blockEntity.useCode(attemptedCode);
        }
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
        if (receiver.isServer()) {
            try {
                var code = payload.getByteArray("Code");
                List<Byte> codeList = new ArrayList<>();
                for (byte b : code)
                    codeList.add(b);

                useCode(codeList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
