package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SlotWrapper extends Slot {
    final Slot target;
    final Pair<ResourceLocation, ResourceLocation> noItemIcon;

    public SlotWrapper(Slot target, int slot, int x, int y, Pair<ResourceLocation, ResourceLocation> noItemIcon) {
        super(target.container, slot, x, y);
        this.target = target;
        this.noItemIcon = noItemIcon;
    }

    public void onTake(Player p_169754_, ItemStack p_169755_) {
        this.target.onTake(p_169754_, p_169755_);
    }

    public boolean mayPlace(ItemStack p_98670_) {
        return this.target.mayPlace(p_98670_);
    }

    public ItemStack getItem() {
        return this.target.getItem();
    }

    public boolean hasItem() {
        return this.target.hasItem();
    }

    public void set(ItemStack p_98679_) {
        this.target.set(p_98679_);
    }

    public void setChanged() {
        this.target.setChanged();
    }

    public int getMaxStackSize() {
        return this.target.getMaxStackSize();
    }

    public int getMaxStackSize(ItemStack p_98675_) {
        return this.target.getMaxStackSize(p_98675_);
    }

    @Nullable
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return this.noItemIcon;
    }

    public ItemStack remove(int p_98663_) {
        return this.target.remove(p_98663_);
    }

    public boolean isActive() {
        return this.target.isActive();
    }

    public boolean mayPickup(Player p_98665_) {
        return this.target.mayPickup(p_98665_);
    }

    @Override
    public int getSlotIndex() {
        return this.target.getSlotIndex();
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return this.target.isSameInventory(other);
    }

    @Override
    public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
        this.target.setBackground(atlas, sprite);
        return this;
    }
}
