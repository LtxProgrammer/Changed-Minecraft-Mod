package net.ltxprogrammer.changed.world.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.FluidCanister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class StasisChamberMenu extends AbstractContainerMenu implements UpdateableMenu {
    public final StasisChamberBlockEntity blockEntity;
    public final Container container;
    public final ContainerData data;
    private @NotNull Player accessor;

    private final Map<Integer, Slot> customSlots = new HashMap<>();
    public int configuredCustomLatex = -1;

    private static final ResourceLocation EMPTY_SYRINGE_SLOT = Changed.modResource("items/empty_slot_syringe");

    public StasisChamberMenu(int id, Inventory inventory, FriendlyByteBuf extra) {
        super(ChangedMenus.STASIS_CHAMBER, id);
        this.accessor = inventory.player;
        this.data = new SimpleContainerData(2);

        if (extra == null) {
            this.blockEntity = null;
            this.container = new SimpleContainer(2);
            this.createSlots(inventory);
            return;
        }

        this.blockEntity = inventory.player.level.getBlockEntity(extra.readBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);
        this.container = blockEntity;
        this.createSlots(inventory);
    }

    public StasisChamberMenu(int id, Inventory inventory, @Nullable StasisChamberBlockEntity blockEntity, ContainerData dataAccess) {
        super(ChangedMenus.STASIS_CHAMBER, id);
        this.accessor = inventory.player;
        this.data = dataAccess;
        this.blockEntity = blockEntity;
        this.container = blockEntity != null ? blockEntity : new SimpleContainer(2);
        this.createSlots(inventory);
    }

    protected void createSlots(Inventory inv) {
        // Transfur variant slot
        this.customSlots.put(0, this.addSlot(new Slot(this.container, 0, 174, 142) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ChangedItems.LATEX_SYRINGE.get() == stack.getItem();
            }

            @Override
            public boolean mayPickup(Player player) {
                return true;
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SYRINGE_SLOT);
            }
        }));
        // Chamber fluid slot TODO some kind of fluid container (not a bucket)
        this.customSlots.put(1, this.addSlot(new Slot(this.container, 1, 196, 142) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof FluidCanister fluidCanister && fluidCanister.getFluid() != null;
            }

            @Override
            public boolean mayPickup(Player player) {
                return blockEntity == null || blockEntity.isDrained();
            }
        }));

        for (int slotY = 0; slotY < 3; ++slotY)
            for (int slotX = 0; slotX < 9; ++slotX)
                this.addSlot(new Slot(inv, slotX + (slotY + 1) * 9, 0 + 8 + slotX * 18, 0 + 84 + slotY * 18));
        for (int slotY = 0; slotY < 9; ++slotY)
            this.addSlot(new Slot(inv, slotY, 0 + 8 + slotY * 18, 0 + 142));
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 1) {
                if (!this.moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                if (index < 1 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 1 + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, 1, 1 + 27, false)) {
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
    protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
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

    public float getFluidLevel(float partialTick) {
        if (blockEntity == null)
            return 0.0f;
        return blockEntity.getFluidLevel(partialTick);
    }

    public Optional<LivingEntity> getChamberedEntity() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getChamberedEntity();
    }

    public Optional<IAbstractChangedEntity> getChamberedLatex() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getChamberedLatex();
    }

    public Optional<StasisChamberBlockEntity.ScheduledCommand> getCurrentCommand() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getCurrentCommand();
    }

    public ImmutableList<StasisChamberBlockEntity.ScheduledCommand> getScheduledCommands() {
        if (blockEntity == null)
            return ImmutableList.of();
        return blockEntity.getScheduledCommands();
    }

    public Optional<TransfurVariant<?>> getConfiguredTransfurVariant() {
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getConfiguredTransfurVariant();
    }

    public int getConfiguredCustomLatex() {
        if (blockEntity == null)
            return 0;
        return blockEntity.getConfiguredCustomLatex();
    }

    public boolean isChamberOpen() {
        if (blockEntity == null)
            return false;
        return blockEntity.getBlockState().getValue(StasisChamber.OPEN);
    }

    public boolean openChamber() {
        if (blockEntity == null)
            return false;
        if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
            chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
        }

        return blockEntity.getBlockState().getValue(StasisChamber.OPEN);
    }

    public boolean closeChamber() {
        if (blockEntity == null)
            return false;
        if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
            chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
        }

        return !blockEntity.getBlockState().getValue(StasisChamber.OPEN);
    }

    @Override
    public int getId() {
        return containerId;
    }

    @Override
    public Player getPlayer() {
        return accessor;
    }

    public enum Command {
        NOOP(menu -> {}),
        OPEN_DOOR(StasisChamberMenu::openChamber),
        CLOSE_DOOR(StasisChamberMenu::closeChamber);

        private final Consumer<StasisChamberMenu> handler;

        Command(Consumer<StasisChamberMenu> handler) {
            this.handler = handler;
        }

        public void handle(StasisChamberMenu menu) {
            handler.accept(menu);
        }
    }

    public void sendSimpleCommand(Command command) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "command");
        tag.putInt("command", command.ordinal());
        this.setDirty(tag);
    }

    public void requestUpdate() {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "update");
        this.setDirty(tag);
    }

    public void inputProgram(String program) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "program");
        tag.putString("program", program);
        this.setDirty(tag);
    }

    public void inputCustomLatexConfig(int configuredCustomLatex) {
        CompoundTag tag = new CompoundTag();
        tag.putString("control", "config");
        tag.putInt("customLatex", configuredCustomLatex);
        this.setDirty(tag);
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && blockEntity != null) {
            String control = payload.getString("control");
            if ("command".equals(control)) {
                int commandId = payload.getInt("command");
                if (commandId < 0 || commandId >= Command.values().length)
                    return;
                Command.values()[commandId].handle(this);
            } else if ("program".equals(control)) {
                String program = payload.getString("program");
                blockEntity.inputProgram(program);
            } else if ("config".equals(control)) {
                if (payload.contains("customLatex"))
                    blockEntity.setConfiguredCustomLatex(payload.getInt("customLatex"));
            }
        }
    }
}
