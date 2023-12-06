package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.LatexSyringe;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DroppedSyringeBlockEntity extends BlockEntity {
    @NotNull
    private LatexVariant<?> variant = LatexVariant.FALLBACK_VARIANT;

    public DroppedSyringeBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.DROPPED_SYRINGE.get(), pos, state);
    }

    public ItemStack getSyringe() {
        return Syringe.setUnpureVariant(new ItemStack(ChangedItems.LATEX_SYRINGE.get()), variant.getFormId());
    }

    public @NotNull LatexVariant<?> getVariant() {
        return variant;
    }

    public void setVariant(LatexVariant<?> variant) {
        this.variant = variant;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("variant", ChangedRegistry.LATEX_VARIANT.get().getID(variant));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        variant = ChangedRegistry.LATEX_VARIANT.get().getValue(tag.getInt("variant"));
    }
}
