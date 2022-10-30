package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ComputerMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>>, SpecialLoadingMenu {
    public final static HashMap<String, Object> guistate = new HashMap<>();

    private ItemStack serverDisk;

    private IItemHandler internal;
    public final Container container;
    public final ContainerData data;
    public final Level world;
    public final Player entity;
    public int x, y, z;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public ComputerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, new SimpleContainer(1), new SimpleContainerData(1));
    }

    public ComputerMenu(int id, Inventory inv, Container p_38971_, ContainerData p_38972_) {
        super(ChangedMenus.COMPUTER, id);
        this.container = p_38971_;
        this.data = p_38972_;
        this.world = inv.player.level;
        this.entity = inv.player;
        this.internal = new ItemStackHandler(1);
        this.customSlots.put(0, this.addSlot(new Slot(p_38971_, 0, 9999, 9999) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        }));
    }

    public ComputerMenu setDisk(ItemStack disk) {
        serverDisk = disk;
        return this;
    }

    public ItemStack getDisk() {
        return this.getSlot(0).getItem();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }

    @Override
    public void afterInit(AbstractContainerMenu menu) {
        this.internal.insertItem(0, serverDisk, false);
        this.slots.get(0).set(serverDisk);
        this.sendAllDataToRemote();
    }
}
