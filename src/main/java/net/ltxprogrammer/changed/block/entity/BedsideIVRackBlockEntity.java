package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.BedsideIVRack;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.UUID;

public class BedsideIVRackBlockEntity extends BlockEntity implements Container, StackedContentsCompatible {
    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    public int tickCount = 0;
    public final int TRANSFUR_REQUIRED_TIME = 20 * 20; // 20 seconds

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {
        @SubscribeEvent
        public static void onSleepingTimeCheck(SleepingTimeCheckEvent event) {
            if (ProcessTransfur.isPlayerLatex(event.getPlayer()))
                return;
            if (event.getSleepingLocation().isEmpty())
                return;
            BlockPos blockPos = event.getSleepingLocation().get();
            Level level = event.getEntity().getLevel();
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                if (level.getBlockEntity(blockPos.relative(direction)) instanceof BedsideIVRackBlockEntity ivRack) {
                    if (ivRack.items.get(0).isEmpty())
                        continue;
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BedsideIVRackBlockEntity ivRack) {
        // Handle Blockstate
        if (ivRack.items.get(0).isEmpty() && state.getValue(BedsideIVRack.FULL)) {
            level.setBlockAndUpdate(pos, state.setValue(BedsideIVRack.FULL, false));
        }

        else if (!ivRack.items.get(0).isEmpty() && !state.getValue(BedsideIVRack.FULL)) {
            level.setBlockAndUpdate(pos, state.setValue(BedsideIVRack.FULL, true));
            level.setBlockAndUpdate(pos.above(), state.setValue(BedsideIVRack.FULL, true).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
        }

        boolean success = false;
        CompoundTag tag = ivRack.items.get(0).getOrCreateTag();
        Optional<UUID> owner = tag.contains("owner") ? Optional.of(tag.getUUID("owner")) : Optional.empty();
        if (!ivRack.items.get(0).isEmpty() && ivRack.items.get(0).is(ChangedItems.LATEX_SYRINGE.get())) {
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                BlockPos adjacent = pos.relative(direction);
                for (Player player : level.getEntities(EntityTypeTest.forClass(Player.class), new AABB(adjacent), EntitySelector.NO_SPECTATORS)) {
                    if (!player.isSleeping())
                        continue;
                    if (owner.isPresent() && !player.getUUID().equals(owner.get()))
                        continue;

                    if (ProcessTransfur.isPlayerLatex(player))
                        continue;

                    if (player.getSleepTimer() > 95 && ivRack.tickCount < ivRack.TRANSFUR_REQUIRED_TIME && !ivRack.items.get(0).isEmpty()) {
                        ivRack.tickCount++;
                        player.sleepCounter = 95;
                        success = true;
                        ivRack.setChanged();
                    }

                    else if (player.getSleepTimer() > 95 && !ivRack.items.get(0).isEmpty()) {
                        ivRack.tickCount = 0;
                        ivRack.items.set(0, ItemStack.EMPTY);
                        try {
                            ResourceLocation formLocation = new ResourceLocation(tag.getString("form"));
                            if (formLocation.equals(LatexVariant.SPECIAL_LATEX))
                                formLocation = Changed.modResource("special/form_" + player.getUUID());
                            ProcessTransfur.setPlayerLatexVariant(player, ChangedRegistry.LATEX_VARIANT.get().getValue(formLocation));
                            if (tag.contains("safe") && tag.getBoolean("safe"))
                                Pale.tryCure(player);
                        } catch (NullPointerException unused) {
                        }
                        ivRack.items.get(0).shrink(1);
                        ivRack.setChanged();
                    }
                }
            }
        }

        if (!success) {
            ivRack.tickCount = 0;
        }
    }

    public BedsideIVRackBlockEntity(BlockPos p_154992_, BlockState p_154993_) {
        super(ChangedBlockEntities.BEDSIDE_IV_RACK.get(), p_154992_, p_154993_);
    }

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
            this.setChanged();
        }

    }

    public int getContainerSize() {
        return this.items.size();
    }

    public void load(CompoundTag p_155025_) {
        super.load(p_155025_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.tickCount = p_155025_.getInt("TickCount");
        ContainerHelper.loadAllItems(p_155025_, this.items);
    }

    protected void saveAdditional(CompoundTag p_187452_) {
        super.saveAdditional(p_187452_);
        p_187452_.putInt("TickCount", this.tickCount);
        ContainerHelper.saveAllItems(p_187452_, this.items);
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
