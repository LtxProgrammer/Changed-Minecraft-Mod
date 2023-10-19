package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.world.inventory.PurifierMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PurifierBlockEntity extends BaseContainerBlockEntity implements StackedContentsCompatible {
    public PurifierBlockEntity(BlockPos p_155545_, BlockState p_155546_) {
        super(ChangedBlockEntities.PURIFIER.get(), p_155545_, p_155546_);
    }

    protected @NotNull Component getDefaultName() {
        return new TranslatableComponent("container.changed.purifier");
    }

    protected @NotNull AbstractContainerMenu createMenu(int p_59293_, @NotNull Inventory p_59294_) {
        return new PurifierMenu(p_59293_, p_59294_, this, this.dataAccess);
    }

    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    int purifyProgress;
    public static final int PURIFY_GOO_PROGRESS_TOTAL_MIN = 20 * 20; // 20 seconds to purify one goo
    public static final int PURIFY_GOO_PROGRESS_TOTAL_MAX = 4 * 60 * 20; // 4 minutes to purify full goo stack
    public static final int PURIFY_SYRINGE_PROGRESS_TOTAL = 2 * 60 * 20; // 2 minutes to purify
    public static final int PURIFY_UNIVERSAL_SYRINGE_PROGRESS_TOTAL = 4 * 60 * 20; // 2 minutes to purify
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int p_58431_) {
            return PurifierBlockEntity.this.purifyProgress;
        }

        public void set(int p_58433_, int p_58434_) {
            PurifierBlockEntity.this.purifyProgress = p_58434_;
        }

        public int getCount() {
            return 1;
        }
    };

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int p_58328_) {
        return this.items.get(p_58328_);
    }

    public ItemStack removeItem(int p_58330_, int p_58331_) {
        return ContainerHelper.removeItem(this.items, p_58330_, p_58331_);
    }

    public ItemStack removeItemNoUpdate(int p_58387_) {
        return ContainerHelper.takeItem(this.items, p_58387_);
    }

    public void setItem(int p_58333_, ItemStack p_58334_) {
        ItemStack itemstack = this.items.get(p_58333_);
        boolean flag = !p_58334_.isEmpty() && p_58334_.sameItem(itemstack) && ItemStack.tagMatches(p_58334_, itemstack);
        this.items.set(p_58333_, p_58334_);
        if (p_58334_.getCount() > this.getMaxStackSize()) {
            p_58334_.setCount(this.getMaxStackSize());
        }

        if (p_58333_ == 0 && !flag) {
            this.purifyProgress = 0;
            this.setChanged();
        }

    }

    public int getContainerSize() {
        return this.items.size();
    }

    public void load(CompoundTag p_155025_) {
        super.load(p_155025_);
        this.purifyProgress = p_155025_.getInt("PurifyTime");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155025_, this.items);
    }

    protected void saveAdditional(CompoundTag p_187452_) {
        super.saveAdditional(p_187452_);
        p_187452_.putInt("PurifyTime", this.purifyProgress);
        ContainerHelper.saveAllItems(p_187452_, this.items);
    }

    public static boolean isImpureGoo(ItemStack itemStack) {
        for (var type : LatexType.values()) {
            if (type == LatexType.NEUTRAL)
                continue;
            if (itemStack.is(type.goo.get()))
                return true;
        }
        return false;
    }

    public static float getTotalProgress(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (!stack.isEmpty() && stack.is(ChangedItems.LATEX_SYRINGE.get()) && tag != null && !tag.getBoolean("safe")) {
            return tag.contains("owner") ? PURIFY_SYRINGE_PROGRESS_TOTAL : PURIFY_UNIVERSAL_SYRINGE_PROGRESS_TOTAL;
        } else if (!stack.isEmpty() && isImpureGoo(stack)) {
            float count = stack.getCount();
            return Mth.lerp(count / stack.getMaxStackSize(), PURIFY_GOO_PROGRESS_TOTAL_MIN, PURIFY_GOO_PROGRESS_TOTAL_MAX);
        }

        return Float.MAX_VALUE;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PurifierBlockEntity blockEntity) {
        ItemStack itemstack = blockEntity.items.get(0);
        CompoundTag tag = itemstack.getTag();
        float progressRelative = getTotalProgress(itemstack);
        if (!itemstack.isEmpty() && itemstack.is(ChangedItems.LATEX_SYRINGE.get()) && tag != null) {
            if (!tag.getBoolean("safe")) {
                ++blockEntity.purifyProgress;
                if (blockEntity.purifyProgress >= progressRelative) {
                    blockEntity.purifyProgress = 0;
                    tag.putBoolean("safe", true);
                }

                blockEntity.setChanged();
            }

            else
                blockEntity.purifyProgress = 0;
        }

        else if (!itemstack.isEmpty() && isImpureGoo(itemstack)) {
            ++blockEntity.purifyProgress;
            if (blockEntity.purifyProgress >= progressRelative) {
                blockEntity.purifyProgress = 0;
                blockEntity.items.set(0, new ItemStack(ChangedItems.LATEX_BASE.get(), itemstack.getCount()));
            }

            blockEntity.setChanged();
        }

        else
            blockEntity.purifyProgress = 0;
    }

    public boolean stillValid(Player p_58340_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return p_58340_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean canPlaceItem(int p_58389_, ItemStack p_58390_) {
        if (p_58389_ == 0) {
            return p_58390_.is(ChangedItems.LATEX_SYRINGE.get());
        }

        else
            return false;
    }

    public void clearContent() {
        this.items.clear();
    }

    public void fillStackedContents(StackedContents p_58342_) {
        for(ItemStack itemstack : this.items) {
            p_58342_.accountStack(itemstack);
        }

    }
}
