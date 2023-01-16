package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CentaurSaddleMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.centaur_saddle");

    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final static String SADDLE_LOCATION = Changed.modResourceStr("saddle");
    public final static String CHEST_LOCATION = Changed.modResourceStr("chest");

    public final Level world;
    public final Player player;
    public int x, y, z;

    private IItemHandler internal;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    private boolean bound = false;

    public CentaurSaddleMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedMenus.CENTAUR_SADDLE, id);

        this.player = inv.player;
        this.world = inv.player.level;

        this.internal = new ItemStackHandler(2);

        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }

        if (pos != null) {
            if (extraData.readableBytes() == 1) { // bound to item
                byte hand = extraData.readByte();
                ItemStack itemstack;
                if (hand == 0)
                    itemstack = this.player.getMainHandItem();
                else
                    itemstack = this.player.getOffhandItem();
                itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
                    this.internal = capability;
                    this.bound = true;
                });
            } else if (extraData.readableBytes() > 1) {
                extraData.readByte(); // drop padding
                Entity entity = world.getEntity(extraData.readVarInt());
                if (entity != null)
                    entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
            } else { // might be bound to block
                BlockEntity ent = inv.player != null ? inv.player.level.getBlockEntity(pos) : null;
                if (ent != null) {
                    ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
                        this.internal = capability;
                        this.bound = true;
                    });
                }
            }
        }

        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 74, 14) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return stack.is(Items.SADDLE);
            }
        }));

        this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 92, 14) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return stack.is(Items.CHEST);
            }
        }));

        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 3 + 8 + sj * 18, -43 + 84 + si * 18));

        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 3 + 8 + si * 18, -43 + 142));


        CompoundTag tag = player.getPersistentData();
        if (tag.contains(SADDLE_LOCATION))
            internal.insertItem(0, ItemStack.of(tag.getCompound(SADDLE_LOCATION)), false);
        if (tag.contains(CHEST_LOCATION))
            internal.insertItem(1, ItemStack.of(tag.getCompound(CHEST_LOCATION)), false);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                if (index < 2 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 2 + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, 2, 2 + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack item, int p_38905_, int p_38906_, boolean p_38907_) {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }
        if (item.isStackable()) {
            while (!item.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(item, itemstack)) {
                    int j = itemstack.getCount() + item.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), item.getMaxStackSize());
                    if (j <= maxSize) {
                        item.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        item.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!item.isEmpty()) {
            if (p_38907_) {
                i = p_38906_ - 1;
            } else {
                i = p_38905_;
            }
            while (true) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(item)) {
                    if (item.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(item.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(item.split(item.getCount()));
                    }
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);

        /*if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
                for (int j = 0; j < internal.getSlots(); ++j) {
                    playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
                }
            } else {
                for (int i = 0; i < internal.getSlots(); ++i) {
                    playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
                }
            }
        }*/
    }

    public Map<Integer, Slot> get() {
        return customSlots;
    }

    public void tick(Player player) {
        CompoundTag old = player.getPersistentData().copy();
        CompoundTag tag = player.getPersistentData();
        if (!internal.getStackInSlot(0).isEmpty())
            tag.put(SADDLE_LOCATION, internal.getStackInSlot(0).serializeNBT());
        else {
            tag.remove(SADDLE_LOCATION);
            player.ejectPassengers();
        }
        if (!internal.getStackInSlot(1).isEmpty())
            tag.put(CHEST_LOCATION, internal.getStackInSlot(1).serializeNBT());
        else {
            tag.remove(CHEST_LOCATION);
        }

        if (!old.equals(tag)) {
            if (player.level.isClientSide)
                Changed.PACKET_HANDLER.sendToServer(SyncTransfurPacket.Builder.of(player));
            else
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
        }
    }
}