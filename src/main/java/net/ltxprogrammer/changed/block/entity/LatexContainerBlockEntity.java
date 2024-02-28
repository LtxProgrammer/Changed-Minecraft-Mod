package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.item.AbstractGooBucket;
import net.ltxprogrammer.changed.item.AbstractGooItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LatexContainerBlockEntity extends BlockEntity {
    private GooType currentType = GooType.NEUTRAL;
    private byte fillLevel = 0;

    public LatexContainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.LATEX_CONTAINER.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("GooType", currentType.name());
        tag.putByte("FillLevel", fillLevel);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("GooType")) currentType = GooType.valueOf(tag.getString("GooType"));
        if (tag.contains("FillLevel")) fillLevel = tag.getByte("FillLevel");
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("LatexType", currentType.name());
        tag.putByte("FillLevel", fillLevel);
        return tag;
    }

    @Nullable
    public ItemStack tryUse(ItemStack itemStack) {
        // Remove goo
        if (itemStack.isEmpty()) {
            if (currentType == GooType.NEUTRAL || fillLevel == 0)
                return null;
            else {
                fillLevel--;
                this.markUpdated();

                return new ItemStack(currentType.goo.get());
            }
        } else if (itemStack.is(Items.BUCKET)) {
            if (currentType == GooType.NEUTRAL || fillLevel < 4)
                return null;
            else {
                fillLevel -= 4;
                this.markUpdated();

                itemStack.shrink(1);
                return new ItemStack(currentType.gooBucket.get());
            }
        }

        // Insert goo
        if (itemStack.getItem() instanceof AbstractGooItem goo) {
            var type = goo.getLatexType();
            if (type == GooType.NEUTRAL || fillLevel >= 16)
                return null;
            if (currentType == GooType.NEUTRAL || currentType == type || fillLevel == 0) {
                currentType = type;
                fillLevel++;
                this.markUpdated();

                itemStack.shrink(1);
                return ItemStack.EMPTY;
            }
        } else if (itemStack.getItem() instanceof AbstractGooBucket latexBucket) {
            var type = latexBucket.getLatexType();
            if (type == null || type == GooType.NEUTRAL || fillLevel > 12)
                return null;
            if (currentType == GooType.NEUTRAL || currentType == type || fillLevel == 0) {
                currentType = type;
                fillLevel += 4;
                this.markUpdated();

                itemStack.shrink(1);
                return new ItemStack(Items.BUCKET);
            }
        }

        return null;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public GooType getFillType() {
        return currentType;
    }
}
