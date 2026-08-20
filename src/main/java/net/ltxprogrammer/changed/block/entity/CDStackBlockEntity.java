package net.ltxprogrammer.changed.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

public class CDStackBlockEntity extends BlockEntity {
    private static final CompoundTag EMPTY_TAG = new CompoundTag();

    protected Stack<CompoundTag> disks = new Stack<>();

    public CDStackBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.CD_STACK.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag listOfDisks = new ListTag();
        listOfDisks.addAll(disks);
        tag.put("Disks", listOfDisks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        disks.clear();
        for (var disk : tag.getList("Disks", 10)) {
            CompoundTag diskData = (CompoundTag) disk;
            if (diskData.isEmpty())
                disks.push(EMPTY_TAG);
            else
                disks.push(diskData);
        }
    }

    public int size() {
        return disks.size();
    }

    public boolean push(ItemStack compactDisc) {
        if (disks.size() >= 16)
            return false;
        if (!compactDisc.is(ChangedItems.COMPACT_DISC.get()))
            return false;

        var tag = compactDisc.getTag();
        if (tag == null || tag.getCompound("fs").isEmpty())
            disks.push(EMPTY_TAG);
        else
            disks.push(tag.getCompound("fs"));

        return true;
    }

    public @Nullable ItemStack pop() {
        if (disks.empty())
            return null;
        var fsTag = disks.pop();
        var compactDisc = new ItemStack(ChangedItems.COMPACT_DISC.get());
        if (fsTag != EMPTY_TAG)
            compactDisc.getOrCreateTag().put("fs", fsTag);
        return compactDisc;
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> list = new ObjectArrayList<>();

        for (;;) {
            boolean goNext = false;
            ItemStack nextItem = pop();
            if (nextItem == null)
                break;

            for (var existingStack : list) {
                if (ItemStack.isSameItemSameTags(nextItem, existingStack)) {
                    existingStack.grow(1);
                    goNext = true;
                    break;
                }
            }

            if (goNext)
                continue;

            list.add(nextItem);
        }

        return list;
    }
}
