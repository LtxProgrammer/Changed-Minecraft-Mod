package net.ltxprogrammer.changed.world.inventory;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class StasisChamberMenu extends AbstractContainerMenu implements UpdateableMenu {
    public final StasisChamberBlockEntity blockEntity;
    private @NotNull Player accessor;

    private @Nullable List<StasisChamberBlockEntity.ScheduledCommand> scheduledCommands = null;
    private @Nullable StasisChamberBlockEntity.ScheduledCommand currentCommand = null;

    private @Nullable TransfurVariant<?> configuredVariant = null;
    public int configuredCustomLatex = -1;

    public StasisChamberMenu(int id, Inventory inventory, FriendlyByteBuf extra) {
        super(ChangedMenus.STASIS_CHAMBER, id);
        this.accessor = inventory.player;

        if (extra == null) {
            this.blockEntity = null;
            return;
        }

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

    public Optional<StasisChamberBlockEntity.ScheduledCommand> getCurrentCommand() {
        if (currentCommand != null)
            return Optional.of(currentCommand);
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getCurrentCommand();
    }

    public ImmutableList<StasisChamberBlockEntity.ScheduledCommand> getScheduledCommands() {
        if (scheduledCommands != null)
            return ImmutableList.copyOf(scheduledCommands);
        if (blockEntity == null)
            return ImmutableList.of();
        return blockEntity.getScheduledCommands();
    }

    public Optional<TransfurVariant<?>> getConfiguredTransfurVariant() {
        if (configuredVariant != null)
            return Optional.of(configuredVariant);
        if (blockEntity == null)
            return Optional.empty();
        return blockEntity.getConfiguredTransfurVariant();
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

    public void updateChamberStatus() {
        if (blockEntity == null)
            return;

        CompoundTag tag = new CompoundTag();
        tag.putString("control", "update");
        var commandTag = new ListTag();
        blockEntity.getConfiguredTransfurVariant().ifPresent(variant -> tag.putInt("transfurVariant",
                ChangedRegistry.TRANSFUR_VARIANT.get().getID(variant)));
        tag.putInt("configuredCustomLatex", blockEntity.getConfiguredCustomLatex());
        blockEntity.getScheduledCommands().stream().map(command -> StringTag.valueOf(command.name())).forEach(commandTag::add);
        tag.put("scheduledCommands", commandTag);
        blockEntity.getCurrentCommand().ifPresent(command -> tag.putString("currentCommand", command.name()));
        this.setDirty(tag);
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && blockEntity != null) {
            String control = payload.getString("control");
            if ("update".equals(control)) {
                updateChamberStatus();
            } else if ("command".equals(control)) {
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
        } else if (receiver.isClient()) {
            String control = payload.getString("control");
            if ("update".equals(control)) {
                if (payload.contains("transfurVariant"))
                    configuredVariant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(payload.getInt("transfurVariant"));
                else
                    configuredVariant = null;
                if (payload.contains("configuredCustomLatex"))
                    configuredCustomLatex = payload.getInt("configuredCustomLatex");
                else
                    configuredCustomLatex = 0;
                scheduledCommands = new ArrayList<>();
                var commandTag = payload.getList("scheduledCommands", 8);
                for (int idx = 0; idx < commandTag.size(); ++idx)
                    scheduledCommands.add(StasisChamberBlockEntity.ScheduledCommand.valueOf(commandTag.getString(idx)));
                currentCommand = null;
                if (payload.contains("currentCommand")) {
                    currentCommand = StasisChamberBlockEntity.ScheduledCommand.valueOf(payload.getString("currentCommand"));
                }
            }
        }
    }
}
