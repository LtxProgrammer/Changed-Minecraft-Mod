package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DroppedSyringeBlockEntity extends BlockEntity {
    private TransfurVariant<?> variant = ChangedTransfurVariants.FALLBACK_VARIANT.get();

    public DroppedSyringeBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.DROPPED_SYRINGE.get(), pos, state);
    }

    public ItemStack getSyringe() {
        return Syringe.setUnpureVariant(new ItemStack(ChangedItems.LATEX_SYRINGE.get()), getVariant().getFormId());
    }

    public @NotNull TransfurVariant<?> getVariant() {
        if (variant != null)
            return variant;
        else
            return ChangedTransfurVariants.FALLBACK_VARIANT.get();
    }

    public void setVariant(TransfurVariant<?> variant) {
        if (this.variant == variant) return;

        this.variant = variant;
        this.setChanged();
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("variant", ChangedRegistry.TRANSFUR_VARIANT.get().getID(getVariant()));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        setVariant(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(tag.getInt("variant")));
    }
}
