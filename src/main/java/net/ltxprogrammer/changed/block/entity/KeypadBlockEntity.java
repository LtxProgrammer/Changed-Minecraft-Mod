package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.block.KeypadBlock;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.world.inventory.KeypadMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeypadBlockEntity extends BlockEntity implements MenuProvider {
    public byte[] code = null;

    public KeypadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.KEYPAD.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (code != null)
            tag.putByteArray("Code", code);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Code"))
            code = tag.getByteArray("Code");
    }

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.keypad");
    @Override
    public Component getDisplayName() {
        return CONTAINER_TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new KeypadMenu(id, inv, this.worldPosition, this.getBlockState(), this);
    }

    private void playSound(SoundEvent event, float volume, float pitch) {
        if (level.getServer() != null)
            ChangedSounds.broadcastSound(level.getServer(), event, this.worldPosition, volume, pitch);
    }

    private void playUnlockSuccess() {
        playSound(ChangedSounds.CHIME2, 1, 1);
    }

    private void playUnlockFail() {
        playSound(ChangedSounds.BUZZER1, 1, 1);
    }

    private void playLock() {
        playSound(ChangedSounds.KEY, 1, 1);
    }

    private void setCode(List<Byte> newCode) {
        if (code != null)
            return;
        code = new byte[newCode.size()];
        for (int i = 0; i < code.length; ++i)
            code[i] = newCode.get(i);
    }

    public void useCode(List<Byte> attemptedCode) {
        if (level == null)
            return;

        if (this.code == null) { // Set code and lock
            setCode(attemptedCode);
            level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(KeypadBlock.POWERED, Boolean.FALSE));
            this.setBlockState(this.getBlockState().setValue(KeypadBlock.POWERED, Boolean.FALSE));
            this.playLock();
            return;
        }

        if (!this.getBlockState().getValue(KeypadBlock.POWERED)) {
            if (attemptedCode.size() != code.length) {
                this.playUnlockFail();
                return;
            }

            for (int idx = 0; idx < code.length; ++idx) {
                if (attemptedCode.get(idx) != code[idx]) {
                    this.playUnlockFail();
                    return;
                }
            }

            var nState = this.getBlockState().setValue(KeypadBlock.POWERED, Boolean.TRUE);
            level.setBlockAndUpdate(this.worldPosition, nState);
            ChangedBlocks.KEYPAD.get().updateNeighbours(nState, level, worldPosition);
            level.scheduleTick(worldPosition, this.getBlockState().getBlock(), 20);
            this.playLock();
        }
    }
}
