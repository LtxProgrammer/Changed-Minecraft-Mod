package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DroppedSyringeBlockEntity extends BlockEntity {
    @NotNull
    private TransfurVariant<?> variant = TransfurVariant.FALLBACK_VARIANT;

    public DroppedSyringeBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.DROPPED_SYRINGE.get(), pos, state);
    }

    public ItemStack getSyringe() {
        return Syringe.setUnpureVariant(new ItemStack(ChangedItems.LATEX_SYRINGE.get()), variant.getFormId());
    }

    public @NotNull TransfurVariant<?> getVariant() {
        return variant;
    }

    public void setVariant(TransfurVariant<?> variant) {
        this.variant = variant;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("variant", ChangedRegistry.TRANSFUR_VARIANT.get().getID(variant));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(tag.getInt("variant"));
    }
}
