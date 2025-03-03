package net.ltxprogrammer.changed.world.inventory;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AccessoryAccessMenu extends AbstractContainerMenu {
    static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public final Player owner;
    public final Inventory inventory;
    public final AccessorySlots accessorySlots;

    private final Set<AccessorySlotType> builtSlots = new HashSet<>();
    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public AccessoryAccessMenu(int id, Player owner) {
        super(ChangedMenus.ACCESSORY_ACCESS, id);
        this.owner = owner;
        this.inventory = owner.getInventory();
        this.accessorySlots = AccessorySlots.getForEntity(owner).orElseGet(AccessorySlots::new);
        this.createSlots(inventory);
    }

    public AccessoryAccessMenu(int id, Inventory inventory, FriendlyByteBuf extra) {
        this(id, inventory.player);
    }

    protected void makeAccessorySlots() {
        for (int si = 0; si < accessorySlots.getContainerSize(); ++si) {
            final AccessorySlotType slotType = Objects.requireNonNull(this.accessorySlots.getSlotTypeByIndex(si));
            this.customSlots.put(si, this.addSlot(new Slot(this.accessorySlots, si, 77 + ((si % 5) * 18), 8 + ((si / 5) * 18)) {
                @Override
                public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, slotType.getNoItemIcon());
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return super.mayPlace(stack) && slotType.canHoldItem(stack);
                }
            }));
        }

        accessorySlots.getSlotTypes().forEach(builtSlots::add);
    }

    protected void createSlots(Inventory inv) {
        for (int si = 0; si < 4; ++si) {
            final EquipmentSlot equipmentSlot = SLOT_IDS[si];
            this.addSlot(new Slot(inv, 39 - si, 8, 8 + (si * 18)) {
                public int getMaxStackSize() {
                    return 1;
                }

                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentSlot, owner);
                }

                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()]);
                }
            });
        }

        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 8 + si * 18, 142));

        this.makeAccessorySlots();
    }

    @Override
    public boolean stillValid(Player player) {
        var testSet = new HashSet<>(builtSlots);
        return accessorySlots.getSlotTypes().allMatch(testSet::remove) && testSet.isEmpty();
    }

    public EquipmentSlot denyInvalidArmorSlot(ItemStack itemStack) {
        var slot = Mob.getEquipmentSlotForItem(itemStack);
        if (slot.getType() != EquipmentSlot.Type.ARMOR)
            return slot;

        return ProcessTransfur.ifPlayerTransfurred(this.owner, variant -> {
            return variant.canWear(this.owner, itemStack, slot) ? slot : EquipmentSlot.MAINHAND;
        }, () -> slot);
    }

    /**
     * Moves the given stack into the first available slots in the range
     * @param stack item stack
     * @param slotRangeStart range start (inclusive)
     * @param slotRangeEnd range end (exclusive)
     * @param reversed iterate in reverse, end to start
     * @return true if the stack was moved partially, or completely. false if no items were moved
     */
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int slotRangeStart, int slotRangeEnd, boolean reversed) {
        return super.moveItemStackTo(stack, slotRangeStart, slotRangeEnd, reversed);
    }

    // 0-3 -> armor, 4-30 -> hotbar, 31->39 -> inventory, 40+ -> accessories
    public ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot != null && slot.hasItem()) {
            ItemStack oldStack = slot.getItem();
            stack = oldStack.copy();
            EquipmentSlot equipmentslot = denyInvalidArmorSlot(stack);
            Set<AccessorySlotType> accessorySlotTypes = accessorySlots.getAccessorySlotsForItem(stack);
            Set<Integer> accessorySlotIndices = accessorySlotTypes.stream().map(accessorySlots::getSlotIndexByType).filter(Objects::nonNull).map(i -> i + 40).collect(Collectors.toSet());
            if (slotId < 4) { // Move out of armor
                if (!this.moveItemStackTo(oldStack, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotId >= 40) { // Move out of accessories
                if (!this.moveItemStackTo(oldStack, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(3 - equipmentslot.getIndex()).hasItem()) { // Move to armor
                int armorSlot = 3 - equipmentslot.getIndex();
                if (!this.moveItemStackTo(oldStack, armorSlot, armorSlot + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!accessorySlotIndices.stream().map(this.slots::get).allMatch(Slot::hasItem)) { // Move to accessory
                for (Integer slotIndex : accessorySlotIndices) {
                    if (!this.slots.get(slotIndex).hasItem()) {
                        if (!this.moveItemStackTo(oldStack, slotIndex, slotIndex + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (slotId >= 4 && slotId < 30) { // Move to hotbar
                if (!this.moveItemStackTo(oldStack, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotId >= 31 && slotId < 40) { // Move from hotbar
                if (!this.moveItemStackTo(oldStack, 4, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(oldStack, 4, 30, false)) {
                return ItemStack.EMPTY;
            }

            if (oldStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (oldStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, oldStack);
            if (slotId == 0) {
                player.drop(oldStack, false);
            }
        }

        return stack;
    }

    public Slot getCustomSlot(int id) {
        return customSlots.get(id);
    }
}
