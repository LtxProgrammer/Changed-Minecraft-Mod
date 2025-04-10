package net.ltxprogrammer.changed.block.entity;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.animation.StasisAnimationParameters;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.fluid.Gas;
import net.ltxprogrammer.changed.fluid.TransfurGas;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.FluidCanister;
import net.ltxprogrammer.changed.item.GasCanister;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class StasisChamberBlockEntity extends BaseContainerBlockEntity implements SeatableBlockEntity, StackedContentsCompatible {
    private SeatEntity entityHolder; // Track single entity when active
    private float fluidLevel = 0.0f; // Allows chamber to fill up with fluid
    private float fluidLevelO = 0.0f;
    private final List<ScheduledCommand> scheduledCommands = new ArrayList<>();
    private @Nullable ScheduledCommand currentCommand = null;
    private LivingEntity cachedEntity;

    public NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int configuredCustomLatex = 0;
    private int waitDuration = 0;
    private boolean stabilized = false;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int dataSlot) {
            return switch (dataSlot) {
                case 0 -> (int)(StasisChamberBlockEntity.this.fluidLevel * 1000);
                case 1 -> StasisChamberBlockEntity.this.configuredCustomLatex;
                default -> 0;
            };
        }

        public void set(int dataSlot, int dataValue) {
            switch (dataSlot) {
                case 0 -> StasisChamberBlockEntity.this.fluidLevel = ((float)dataValue) * 0.001f;
                case 1 -> StasisChamberBlockEntity.this.configuredCustomLatex = dataValue;
            };
        }

        public int getCount() {
            return 2;
        }
    };

    protected @NotNull Component getDefaultName() {
        return new TranslatableComponent("container.changed.stasis_chamber");
    }

    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new StasisChamberMenu(id, inventory, this, this.dataAccess);
    }

    public StasisChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.STASIS_CHAMBER.get(), pos, state);
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

    public void setItem(int slotId, ItemStack stack) {
        ItemStack existingItem = this.items.get(slotId);
        boolean flag = !stack.isEmpty() && stack.sameItem(existingItem) && ItemStack.tagMatches(stack, existingItem);
        this.items.set(slotId, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (slotId == 0 && !flag) {
            this.setChanged();
        }

        if (slotId == 1 && !flag) {
            this.setChanged();
        }
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean canPlaceItem(int slotId, ItemStack stack) {
        if (slotId == 0)
            return stack.is(ChangedItems.LATEX_SYRINGE.get());
        else if (slotId == 1)
            return stack.is(ChangedItems.LATEX_SYRINGE.get()); // TODO
        else
            return false;
    }

    public void clearContent() {
        this.items.clear();
    }

    public void fillStackedContents(StackedContents contents) {
        for(ItemStack itemstack : this.items) {
            contents.accountStack(itemstack);
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("fluidLevel", fluidLevel);
        tag.putFloat("fluidLevelO", fluidLevelO);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("configuredCustomLatex", configuredCustomLatex);
        tag.putInt("waitDuration", waitDuration);
        tag.putBoolean("stabilized", stabilized);

        var commandTag = new ListTag();
        scheduledCommands.stream().map(command -> StringTag.valueOf(command.name())).forEach(commandTag::add);
        tag.put("scheduledCommands", commandTag);
        if (currentCommand != null)
            tag.putString("currentCommand", currentCommand.name());
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        fluidLevel = tag.getFloat("fluidLevel");
        fluidLevelO = tag.getFloat("fluidLevelO");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        configuredCustomLatex = tag.getInt("configuredCustomLatex");
        waitDuration = tag.getInt("waitDuration");
        stabilized = tag.getBoolean("stabilized");

        scheduledCommands.clear();
        var commandTag = tag.getList("scheduledCommands", 8);
        for (int idx = 0; idx < commandTag.size(); ++idx)
            scheduledCommands.add(ScheduledCommand.valueOf(commandTag.getString(idx)));
        currentCommand = null;
        if (tag.contains("currentCommand")) {
            currentCommand = ScheduledCommand.valueOf(tag.getString("currentCommand"));
        }
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public SeatEntity getEntityHolder() {
        return entityHolder;
    }

    @Override
    public void setEntityHolder(SeatEntity entityHolder) {
        this.entityHolder = entityHolder;
    }

    public boolean chamberEntity(LivingEntity entity) {
        if (entityHolder == null || entityHolder.isRemoved()) {
            entityHolder = SeatEntity.createFor(entity.level, this.getBlockState(), this.getBlockPos(), false, true, false);
        }

        if (this.getSeatedEntity() != null)
            return false;
        else if (entityHolder != null) {
            if (!level.isClientSide && getFluidType().orElse(null) instanceof WaterFluid) {
                entity.startRiding(entityHolder);
                ChangedAnimationEvents.broadcastEntityAnimation(entity, ChangedAnimationEvents.STASIS_IDLE.get(), StasisAnimationParameters.INSTANCE);
            }
            return true;
        }

        return false;
    }

    public Optional<LivingEntity> getChamberedEntity() {
        if (entityHolder == null)
            return Optional.empty();
        if (this.getBlockState().getValue(StasisChamber.OPEN))
            return Optional.empty();
        return Optional.ofNullable(entityHolder.getFirstPassenger()).map(entity -> {
            if (entity instanceof LivingEntity livingEntity)
                return livingEntity;
            else
                return null;
        });
    }

    public Optional<IAbstractChangedEntity> getChamberedLatex() {
        return getChamberedEntity().map(IAbstractChangedEntity::forEither);
    }

    public Optional<ScheduledCommand> getCurrentCommand() {
        return Optional.ofNullable(currentCommand);
    }

    private @Nullable TransfurVariant<?> findVariantFromSlots() {
        return items.get(0).is(ChangedItems.LATEX_SYRINGE.get()) ? Syringe.getVariant(items.get(0)) : null;
    }

    public Optional<TransfurVariant<?>> getConfiguredTransfurVariant() {
        return Optional.ofNullable(findVariantFromSlots());
    }

    public ImmutableList<ScheduledCommand> getScheduledCommands() {
        return ImmutableList.copyOf(scheduledCommands);
    }

    public float getFluidLevel() {
        return fluidLevel;
    }

    public float getFluidYHeight() {
        return (getFluidLevel() * 2.75f + 0.125f) + this.getBlockPos().below().getY();
    }

    public float getFluidLevel(float partialTick) {
        return Mth.lerp(partialTick, fluidLevelO, fluidLevel);
    }

    public Optional<Fluid> getFluidType() {
        ItemStack canisterStack = items.get(1);
        if (canisterStack.getCount() > 0 && canisterStack.getItem() instanceof FluidCanister canisterItem) {
            return Optional.ofNullable(canisterItem.getFluid());
        }
        return Optional.empty();
    }

    public void setFluidLevel(float fluidLevel) {
        this.fluidLevelO = fluidLevel;
        this.fluidLevel = fluidLevel;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, StasisChamberBlockEntity blockEntity) {
        var commands = blockEntity.scheduledCommands;
        if (commands.isEmpty() && !blockEntity.getEntitiesWithin().isEmpty()) {
            // No scheduled work, ensure entities within can leave
            commands.add(ScheduledCommand.DRAIN);
            commands.add(ScheduledCommand.RELEASE);
            commands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
        }

        if (!commands.isEmpty() && blockEntity.currentCommand == null) {
            blockEntity.currentCommand = commands.get(0);
            commands.remove(0);

            if (blockEntity.currentCommand == null || !blockEntity.currentCommand.canStart(blockEntity)) {
                blockEntity.currentCommand = null; // Cannot start command
                blockEntity.markUpdated();
                return;
            } else {
                blockEntity.markUpdated();
            }
        }

        if (blockEntity.currentCommand != null && !blockEntity.currentCommand.tick(blockEntity)) {
            blockEntity.currentCommand = null; // Command finished
            blockEntity.markUpdated();
        }
    }

    public boolean isOpen() {
        return this.getBlockState().getValue(StasisChamber.OPEN);
    }

    public boolean isClosed() {
        return !isOpen();
    }

    public boolean isFilled() {
        return fluidLevel >= 1.0f;
    }

    public boolean isPartiallyFilled() {
        return fluidLevel > 0.0f;
    }

    public boolean isClosedAndDrained() {
        return isClosed() && isDrained();
    }

    public boolean isClosedAndNotFull() {
        return isClosed() && !isFilled();
    }

    public boolean isDrained() {
        return fluidLevel <= 0.0f;
    }

    public boolean isFilledAndHasEntity() {
        return isFilled() && getChamberedEntity().isPresent();
    }

    public boolean isFilledAndHasLatex() {
        return isFilled() && getChamberedLatex().isPresent();
    }

    public List<LivingEntity> getEntitiesWithin() {
        AABB boxNS = new AABB(this.getBlockPos()).inflate(0.0, 14.0 / 16.0, 9.3 / 16.0);
        AABB boxEW = new AABB(this.getBlockPos()).inflate(9.3 / 16.0, 14.0 / 16.0, 0.0);
        Level level = getLevel();
        if (level == null)
            return List.of();
        var list = level.getEntitiesOfClass(LivingEntity.class, boxNS);
        level.getEntitiesOfClass(LivingEntity.class, boxEW).forEach(entity -> {
            if (list.contains(entity)) return;
            list.add(entity);
        });
        return list;
    }

    public boolean ensureCapturedIsStillInside() {
        var entities = getEntitiesWithin();

        if (cachedEntity == null) {
            cachedEntity = entities.stream().findAny().orElse(null);
        }

        if (cachedEntity != null) {
            if (cachedEntity.isDeadOrDying())
                return false; // Entity is dead
            if (!entities.contains(cachedEntity))
                return false; // Entity is no longer inside the chamber

            chamberEntity(cachedEntity);
        }

        return cachedEntity != null;
    }

    public int getConfiguredCustomLatex() {
        return configuredCustomLatex;
    }

    public void setConfiguredCustomLatex(int configuredCustomLatex) {
        this.configuredCustomLatex = configuredCustomLatex;
        markUpdated();
    }

    public boolean isStabilized() {
        return stabilized;
    }

    public void trimSchedule() {
        if (currentCommand != null) {
            currentCommand = switch (currentCommand) {
                case CLOSE_WHEN_EMPTY, RELEASE, DRAIN, WAIT -> null;
                default -> currentCommand;
            };
        }
        while (!scheduledCommands.isEmpty()) {
            var lastCommand = scheduledCommands.get(scheduledCommands.size() - 1);
            switch (lastCommand) {
                case CLOSE_WHEN_EMPTY, RELEASE, DRAIN, WAIT -> scheduledCommands.remove(scheduledCommands.size() - 1);
                default -> {
                    return;
                }
            }
        }
    }

    public void inputProgram(String program) {
        if ("transfur".equals(program) && !scheduledCommands.contains(ScheduledCommand.TRANSFUR_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.OPEN);
            scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
            scheduledCommands.add(ScheduledCommand.FILL);
            scheduledCommands.add(ScheduledCommand.TRANSFUR_ENTITY);

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("modify".equals(program) && !scheduledCommands.contains(ScheduledCommand.MODIFY_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.OPEN);
            scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
            scheduledCommands.add(ScheduledCommand.FILL);
            scheduledCommands.add(ScheduledCommand.MODIFY_ENTITY);

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("captureNextEntity".equals(program) && !scheduledCommands.contains(ScheduledCommand.CAPTURE_ENTITY)) {
            trimSchedule();

            scheduledCommands.add(ScheduledCommand.OPEN);
            scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
            scheduledCommands.add(ScheduledCommand.WAIT);
            waitDuration = 200; // Default 10 seconds

            scheduledCommands.add(ScheduledCommand.DRAIN);
            scheduledCommands.add(ScheduledCommand.RELEASE);
            scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
            markUpdated();
        }

        else if ("toggleStasis".equals(program)) {
            if (stabilized || scheduledCommands.contains(ScheduledCommand.STABILIZE_ENTITY) || currentCommand == ScheduledCommand.STABILIZE_ENTITY) {
                trimSchedule();

                currentCommand = ScheduledCommand.DRAIN;
                scheduledCommands.clear();
                scheduledCommands.add(ScheduledCommand.RELEASE);
                scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);

                markUpdated();
            } else {
                trimSchedule();

                scheduledCommands.add(ScheduledCommand.OPEN);
                scheduledCommands.add(ScheduledCommand.CAPTURE_ENTITY);
                scheduledCommands.add(ScheduledCommand.FILL);
                scheduledCommands.add(ScheduledCommand.STABILIZE_ENTITY);
                scheduledCommands.add(ScheduledCommand.WAIT);
                waitDuration = 400; // Default 20 seconds

                scheduledCommands.add(ScheduledCommand.DRAIN);
                scheduledCommands.add(ScheduledCommand.RELEASE);
                scheduledCommands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
                markUpdated();
            }
        }
    }

    public enum ScheduledCommand {
        OPEN(StasisChamberBlockEntity::isClosedAndDrained, blockEntity -> {
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            return false;
        }), // If chamber is closed -> Opens chamber
        CAPTURE_ENTITY(StasisChamberBlockEntity::isOpen, blockEntity -> {
            var entities = blockEntity.getEntitiesWithin();
            if (entities.size() != 1)
                return true; // Execute again
            blockEntity.cachedEntity = entities.get(0);

            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            return false;
        }), // If chamber is open -> Closes chamber when entity is detected within
        CLOSE(StasisChamberBlockEntity::isOpen, blockEntity -> {
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            return false;
        }), // If chamber is open -> Closes chamber
        FILL(StasisChamberBlockEntity::isClosedAndNotFull, blockEntity -> {
            blockEntity.fluidLevelO = blockEntity.fluidLevel;
            blockEntity.fluidLevel += 0.005f; // Take 10 seconds to fill

            if (blockEntity.fluidLevel > 0.6f)
                blockEntity.ensureCapturedIsStillInside();

            if (blockEntity.isFilled()) {
                blockEntity.fluidLevelO = 1f;
                blockEntity.fluidLevel = 1f;
            }

            blockEntity.markUpdated();
            return !blockEntity.isFilled();
        }), // If chamber is closed, and drained -> Fills chamber with fluidItem, then restricts entity with entityHolder
        STABILIZE_ENTITY(StasisChamberBlockEntity::isFilledAndHasEntity, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.stabilized = true;
            blockEntity.getChamberedEntity().map(EntityUtil::playerOrNull).map(Player::getLevel).ifPresent(level -> {
                if (level instanceof ServerLevel serverLevel)
                    serverLevel.updateSleepingPlayerList();
            });

            return false;
        }), // If chamber is filled, and has captured entity -> Disable npc AI or "freeze" players
        MODIFY_ENTITY(StasisChamberBlockEntity::isFilledAndHasLatex, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.getChamberedLatex().ifPresent(entity -> {
                if (entity.getChangedEntity() instanceof CustomLatexEntity customLatexEntity) {
                    customLatexEntity.setRawFormFlags(blockEntity.configuredCustomLatex);
                    ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.POISON, 1.0f, 1.0f);
                } else ChangedTransfurVariants.Gendered.getOpposite(entity.getSelfVariant()).ifPresent(otherVariant -> {
                    entity.replaceVariant(otherVariant);
                    ChangedSounds.broadcastSound(entity.getEntity(), ChangedSounds.POISON, 1.0f, 1.0f);
                });
            });

            return false;
        }), // If chamber is filled, and has captured latex -> Modifies latex with given configuration
        TRANSFUR_ENTITY(blockEntity -> {
            return blockEntity.findVariantFromSlots() != null && blockEntity.isFilledAndHasEntity() && !blockEntity.isFilledAndHasLatex();
        }, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.getChamberedEntity().ifPresent(entity -> {
                if (TransfurVariant.getEntityVariant(entity) != null) return;

                ProcessTransfur.transfur(entity, entity.level, blockEntity.findVariantFromSlots(), true, TransfurContext.hazard(TransfurCause.STASIS_CHAMBER));
                if (entity.isRemoved() || entity.isDeadOrDying()) { // Transfurring killed entity, replaced with npc
                    blockEntity.cachedEntity = null;
                    blockEntity.ensureCapturedIsStillInside();
                }

                blockEntity.setItem(0, new ItemStack(ChangedItems.SYRINGE.get()));
            });

            return blockEntity.getChamberedLatex().map(IAbstractChangedEntity::getTransfurVariantInstance)
                    .map(TransfurVariantInstance::isTransfurring)
                    .orElse(false);
        }), // If chamber is filled, and has captured entity -> Transfurs the entity with given variant
        DRAIN(StasisChamberBlockEntity::isPartiallyFilled, blockEntity -> {
            blockEntity.fluidLevelO = blockEntity.fluidLevel;
            blockEntity.fluidLevel -= 0.01f; // Take 5 seconds to drain

            if (blockEntity.stabilized) {
                blockEntity.stabilized = false;
                blockEntity.getChamberedEntity().map(EntityUtil::playerOrNull).map(Player::getLevel).ifPresent(level -> {
                    if (level instanceof ServerLevel serverLevel)
                        serverLevel.updateSleepingPlayerList();
                });
            }

            if (blockEntity.fluidLevel > 0.6f)
                blockEntity.ensureCapturedIsStillInside();
            else {
                SeatEntity seatEntity = blockEntity.getEntityHolder();
                if (seatEntity != null)
                    seatEntity.getPassengers().forEach(Entity::stopRiding);
            }

            if (blockEntity.isDrained()) {
                blockEntity.fluidLevelO = 0f;
                blockEntity.fluidLevel = 0f;
            }

            blockEntity.markUpdated();
            return !blockEntity.isDrained();
        }), // If chamber is filled -> frees captured entity's autonomy, and drains chamber
        RELEASE(StasisChamberBlockEntity::isClosedAndDrained, blockEntity -> {
            if (blockEntity.getEntitiesWithin().isEmpty())
                return false; // No entities inside, no need to open
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.openDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }
            return false;
        }),
        CLOSE_WHEN_EMPTY(StasisChamberBlockEntity::isOpen, blockEntity -> {
            if (!blockEntity.getEntitiesWithin().isEmpty())
                return true;
            if (blockEntity.getBlockState().getBlock() instanceof StasisChamber chamber) {
                chamber.closeDoor(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            return false;
        }),
        WAIT(blockEntity -> true, blockEntity -> {
            return blockEntity.waitDuration-- > 0;
        }); // Waits a given duration before proceeding to the next command

        private final Predicate<StasisChamberBlockEntity> predicateCanStart;
        private final Function<StasisChamberBlockEntity, Boolean> functionTick;

        ScheduledCommand(Predicate<StasisChamberBlockEntity> predicateCanStart, Function<StasisChamberBlockEntity, Boolean> functionTick) {
            this.predicateCanStart = predicateCanStart;
            this.functionTick = functionTick;
        }

        public boolean canStart(StasisChamberBlockEntity blockEntity) {
            return this.predicateCanStart.test(blockEntity);
        }

        public boolean tick(StasisChamberBlockEntity blockEntity) {
            return this.functionTick.apply(blockEntity);
        }

        public TranslatableComponent getDisplayText() {
            return new TranslatableComponent("changed.stasis.command." + name().toLowerCase());
        }

        public TranslatableComponent getActiveDisplayText() {
            return new TranslatableComponent("changed.stasis.command._active", getDisplayText());
        }
    }
}
