package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.recipe.InfuserRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class InfuserMenu extends RecipeBookMenu<SimpleContainer> implements Supplier<Map<Integer, Slot>>, GuiStateProvider {
    @Override
    public void fillCraftSlotsStackedContents(StackedContents p_40117_) {
        for (int i = 0; i < craftingGrid.getSlots(); i++) {
            p_40117_.accountSimpleStack(craftingGrid.getStackInSlot(i));
        }
    }

    @Override
    public void clearCraftingContent() {
        for (int i = 0; i < craftingGrid.getSlots(); i++)
            craftingGrid.insertItem(i, ItemStack.EMPTY, false);
    }

    @Override
    public boolean recipeMatches(Recipe<? super SimpleContainer> p_40118_) {
        this.syncCopyContainer();
        return p_40118_.matches(this.copyContainer, this.entity.level);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return 3;
    }

    @Override
    public int getGridHeight() {
        return 3;
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return ChangedRecipeTypes.INFUSER_BOOK;
    }

    @Override
    public boolean shouldMoveToInventory(int p_150635_) {
        return p_150635_ != this.getResultSlotIndex();
    }

    @Override
    public HashMap<String, Object> getState() {
        return guiState;
    }

    public static class InfuserSlotItemHandler extends SlotItemHandler {
        final InfuserMenu menu;

        public InfuserSlotItemHandler(InfuserMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.menu = menu;
        }

        public void setChanged() {
            super.setChanged();
            this.menu.slotsChanged(this.container);
        }
    }

    public final HashMap<String, Object> guiState = new HashMap<>();

    public final Level world;
    public final Player entity;
    public int x, y, z;

    //private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
    private final SimpleContainer copyContainer = new SimpleContainer(9);
    private IItemHandler internal;
    private IItemHandler craftingGrid;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    private boolean bound = false;

    private static final int SLOT_INPUT = 10;
    public InfuserMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedMenus.INFUSER, id);

        this.entity = inv.player;
        this.world = inv.player.level;

        this.internal = new ItemStackHandler(2);
        this.craftingGrid = new ItemStackHandler(9);

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
                    itemstack = this.entity.getMainHandItem();
                else
                    itemstack = this.entity.getOffhandItem();
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

        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 133, 33) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                internal.getStackInSlot(1).shrink(stack.getCount());
                for (int j = 0; j < craftingGrid.getSlots(); ++j) {
                    ItemStack item = craftingGrid.getStackInSlot(j);
                    if (item.getItem() instanceof BucketItem) {
                        player.getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET));
                    }
                    else if (item.getItem() instanceof BowlFoodItem) {
                        player.getInventory().placeItemBackInInventory(new ItemStack(Items.BOWL));
                    }
                    craftingGrid.getStackInSlot(j).shrink(1);
                }
            }
        }));
        this.customSlots.put(1, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 0, 61, 15)));
        this.customSlots.put(2, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 1, 61, 33)));
        this.customSlots.put(3, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 2, 61, 51)));
        this.customSlots.put(4, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 3, 79, 15)));
        this.customSlots.put(5, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 4, 79, 33)));
        this.customSlots.put(6, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 5, 79, 51)));
        this.customSlots.put(7, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 6, 97, 15)));
        this.customSlots.put(8, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 7, 97, 33)));
        this.customSlots.put(9, this.addSlot(new InfuserSlotItemHandler(this, craftingGrid, 8, 97, 51)));
        this.customSlots.put(SLOT_INPUT, this.addSlot(new InfuserSlotItemHandler(this, internal, 1, 25, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof Syringe.BloodSyringe || stack.is(Items.ARROW);
            }
        }));

        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 0 + 84 + si * 18));

        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 0 + 142));

    }

    private Gender lastAssembledGender = Gender.MALE;
    private Gender getSelectedGender() {
        String name = Changed.modResource("male_female_switch").toString();
        return guiState.get(name) != null &&
                (Boolean)guiState.get(name) ? Gender.FEMALE : Gender.MALE;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (lastAssembledGender != getSelectedGender()) {
            this.slotsChanged(null);
        }
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 11) {
                if (!this.moveItemStackTo(itemstack1, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 11, false)) {
                if (index < 11 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 11 + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, 11, 11 + 27, false)) {
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

            if (index == 0)
                slot.onTake(playerIn, itemstack);
            else
                slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    public void syncCopyContainer() {
        for (int i = 0; i < craftingGrid.getSlots(); i++) {
            copyContainer.setItem(i, craftingGrid.getStackInSlot(i));
        }
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        if (!this.world.isClientSide) {
            this.syncCopyContainer();

            ServerPlayer serverplayer = (ServerPlayer)this.entity;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<InfuserRecipes.InfuserRecipe> recipe = serverplayer.getLevel().getServer().getRecipeManager()
                    .getRecipeFor(ChangedRecipeTypes.INFUSER_RECIPE, copyContainer, serverplayer.level);
            if (!recipe.isEmpty() && !this.internal.getStackInSlot(1).isEmpty()) {
                Gender gender = getSelectedGender();
                itemstack = InfuserRecipes.InfuserRecipe.getBaseFor(this.internal.getStackInSlot(1));
                itemstack = recipe.get().processItem(itemstack, gender);
                lastAssembledGender = gender;
                if (this.internal.getStackInSlot(1).getTag() != null) {
                    itemstack.getTag().putUUID("owner", this.internal.getStackInSlot(1).getTag().getUUID("owner"));
                }
            }

            this.getSlot(0).set(itemstack);
        }
        super.slotsChanged(container);
    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }
        if (p_38904_.isStackable()) {
            while (!p_38904_.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
                    int j = itemstack.getCount() + p_38904_.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
                    if (j <= maxSize) {
                        p_38904_.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        p_38904_.shrink(maxSize - itemstack.getCount());
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
        if (!p_38904_.isEmpty()) {
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
                if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
                    if (p_38904_.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(p_38904_.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(p_38904_.split(p_38904_.getCount()));
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
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);

        if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
                playerIn.drop(internal.extractItem(1, internal.getStackInSlot(1).getCount(), false), false);
                /*for (int j = 0; j < craftSlots.getHeight() * craftSlots.getWidth(); ++j) {
                    playerIn.drop(craftSlots.removeItemNoUpdate(j), false);
                }*/
                for (int j = 0; j < craftingGrid.getSlots(); ++j) {
                    playerIn.drop(craftingGrid.extractItem(j, craftingGrid.getStackInSlot(j).getCount(), false), false);
                }
            } else {
                playerIn.getInventory().placeItemBackInInventory(internal.extractItem(1, internal.getStackInSlot(1).getCount(), false));
                /*for (int j = 0; j < craftSlots.getHeight() * craftSlots.getWidth(); ++j) {
                    playerIn.getInventory().placeItemBackInInventory(craftSlots.removeItemNoUpdate(j));
                }*/
                for (int j = 0; j < craftingGrid.getSlots(); ++j) {
                    playerIn.getInventory().placeItemBackInInventory(craftingGrid.extractItem(j, craftingGrid.getStackInSlot(j).getCount(), false));
                }
            }
        }
    }

    public Map<Integer, Slot> get() {
        return customSlots;
    }
}