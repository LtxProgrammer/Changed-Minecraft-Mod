package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AccessChestAbilityInstance extends AbstractAbilityInstance implements Container, MenuProvider {
    final NonNullList<ItemStack> itemStacks = NonNullList.withSize(2 * 9, ItemStack.EMPTY);

    public AccessChestAbilityInstance(AbstractAbility<AccessChestAbilityInstance> ability, IAbstractLatex entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        var ability = entity.getAbilityInstance(ChangedAbilities.ACCESS_SADDLE.get());
        return !(ability == null || ability.chest == null || ability.chest.isEmpty());
    }

    @Override
    public boolean canKeepUsing() {
        if (!entity.isStillLatex())
            return false;
        return !entity.getAbilityInstance(ChangedAbilities.ACCESS_SADDLE.get()).chest.isEmpty();
    }

    @Override
    public void startUsing() {
        entity.openMenu(this);
    }

    @Override
    public void tick() {}

    @Override
    public void stopUsing() {}

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        ContainerHelper.saveAllItems(tag, itemStacks);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        ContainerHelper.loadAllItems(tag, itemStacks);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (entity.isDeadOrDying() && entity.getLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM)) {
            clearContent();
            return;
        }

        itemStacks.forEach(itemStack -> {
            entity.drop(itemStack, true);
        });
        clearContent();
    }

    @Override
    public int getContainerSize() {
        return itemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.itemStacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return ContainerHelper.removeItem(itemStacks, slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = this.itemStacks.get(slot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        this.itemStacks.set(slot, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return canKeepUsing();
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.changed.access_chest");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x2, id, inv, this, 2);
    }
}
