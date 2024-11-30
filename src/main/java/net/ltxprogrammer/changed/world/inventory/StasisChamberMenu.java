package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class StasisChamberMenu extends AbstractContainerMenu implements UpdateableMenu {
    private @Nullable StasisChamberBlockEntity blockEntity;
    private @NotNull Player accessor;

    public StasisChamberMenu(int id, Inventory inventory, FriendlyByteBuf extra) {
        super(ChangedMenus.STASIS_CHAMBER, id);
        this.accessor = inventory.player;

        if (extra == null)
            return;

        this.blockEntity = inventory.player.level.getBlockEntity(extra.readBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);
    }

    public StasisChamberMenu(int id, Inventory inventory, @Nullable StasisChamberBlockEntity blockEntity) {
        super(ChangedMenus.STASIS_CHAMBER, id);
        this.accessor = inventory.player;
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null;
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
        tag.putInt("command", command.ordinal());
        this.setDirty(tag);
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && blockEntity != null) {
            int commandId = payload.getInt("command");
            if (commandId < 0 || commandId >= Command.values().length)
                return;
            Command.values()[commandId].handle(this);
        } else if (receiver.isClient()) {

        }
    }
}
