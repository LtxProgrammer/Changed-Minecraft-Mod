package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class StasisChamberBlockEntity extends BlockEntity implements SeatableBlockEntity {
    private SeatEntity entityHolder; // Track single entity when active
    private float fluidLevel = 0.0f; // Allows chamber to fill up with fluid
    private float fluidLevelO = 0.0f;
    private ItemStack fluidItem = ItemStack.EMPTY; // Could be used to fill the chamber with oxygenated fluid for stasis (or latex to transfur)
    private final List<ScheduledCommand> scheduledCommands = new ArrayList<>();
    private @Nullable ScheduledCommand currentCommand = null;
    private LivingEntity cachedEntity;

    private @Nullable TransfurVariant<?> configuredVariant = ChangedTransfurVariants.DARK_LATEX_WOLF_MALE.get();
    private int configuredCustomLatex = 0;
    private int waitDuration = 0;
    private boolean stabilized = false;

    public StasisChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.STASIS_CHAMBER.get(), pos, state);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("fluidLevel", fluidLevel);
        tag.putFloat("fluidLevelO", fluidLevelO);
        tag.put("fluidItem", fluidItem.serializeNBT());
        if (configuredVariant != null)
            TagUtil.putResourceLocation(tag, "configuredVariant", configuredVariant.getFormId());
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
        fluidItem = ItemStack.of(tag.getCompound("fluidItem"));
        if (tag.contains("configuredVariant"))
            configuredVariant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(tag, "configuredVariant"));
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
            entityHolder = SeatEntity.createFor(entity.level, this.getBlockState(), this.getBlockPos(), false, true);
        }

        if (this.getSeatedEntity() != null)
            return false;
        else if (entityHolder != null) {
            if (!level.isClientSide)
                entity.startRiding(entityHolder);
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

    public float getFluidLevel(float partialTick) {
        return Mth.lerp(partialTick, fluidLevel, fluidLevelO);
    }

    public void setFluidLevel(float fluidLevel) {
        this.fluidLevelO = fluidLevel;
        this.fluidLevel = fluidLevel;
    }

    public ItemStack getFluidItem() {
        return fluidItem;
    }

    public void setFluidItem(ItemStack fluidItem) {
        this.fluidItem = fluidItem;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, StasisChamberBlockEntity blockEntity) {
        var commands = blockEntity.scheduledCommands;
        if (commands.isEmpty()) {
            // No scheduled work, return to idle state

            // These are for debugging
            commands.add(ScheduledCommand.OPEN);
            commands.add(ScheduledCommand.CAPTURE_ENTITY);
            commands.add(ScheduledCommand.FILL);
            commands.add(ScheduledCommand.TRANSFUR_ENTITY);
            commands.add(ScheduledCommand.MODIFY_ENTITY);
            // End debug

            commands.add(ScheduledCommand.DRAIN);
            commands.add(ScheduledCommand.RELEASE);
            commands.add(ScheduledCommand.CLOSE_WHEN_EMPTY);
        }

        if (blockEntity.currentCommand == null) {
            blockEntity.currentCommand = commands.get(0);
            commands.remove(0);

            if (blockEntity.currentCommand == null || !blockEntity.currentCommand.canStart(blockEntity)) {
                blockEntity.currentCommand = null; // Cannot start command
                return;
            }
        }

        if (!blockEntity.currentCommand.tick(blockEntity))
            blockEntity.currentCommand = null; // Command finished
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

    public boolean isClosedAndDrained() {
        return isClosed() && isDrained();
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
        AABB box = new AABB(this.getBlockPos()).inflate(0.5, 0.8, 0.5);
        Level level = getLevel();
        if (level == null)
            return List.of();
        return level.getEntitiesOfClass(LivingEntity.class, box);
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
    }

    public boolean isStabilized() {
        return stabilized;
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
        FILL(StasisChamberBlockEntity::isClosedAndDrained, blockEntity -> {
            blockEntity.fluidLevelO = blockEntity.fluidLevel;
            blockEntity.fluidLevel += 0.005f; // Take 10 seconds to fill

            if (blockEntity.fluidLevel > 0.6f)
                blockEntity.ensureCapturedIsStillInside();

            return !blockEntity.isFilled();
        }), // If chamber is closed, and drained -> Fills chamber with fluidItem, then restricts entity with entityHolder
        STABILIZE_ENTITY(StasisChamberBlockEntity::isFilledAndHasEntity, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.stabilized = true;

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
            return blockEntity.configuredVariant != null && blockEntity.isFilledAndHasEntity() && !blockEntity.isFilledAndHasLatex();
        }, blockEntity -> {
            if (!blockEntity.ensureCapturedIsStillInside())
                return false;

            blockEntity.getChamberedEntity().ifPresent(entity -> {
                if (TransfurVariant.getEntityVariant(entity) != null) return;

                ProcessTransfur.transfur(entity, entity.level, blockEntity.configuredVariant, true, TransfurContext.hazard(TransfurCause.STASIS_CHAMBER));
                if (entity.isRemoved() || entity.isDeadOrDying()) { // Transfurring killed entity, replaced with npc
                    blockEntity.cachedEntity = null;
                    blockEntity.ensureCapturedIsStillInside();
                }

                blockEntity.configuredVariant = null;
            });

            return blockEntity.getChamberedLatex().map(IAbstractChangedEntity::getTransfurVariantInstance)
                    .map(TransfurVariantInstance::isTransfurring)
                    .orElse(false);
        }), // If chamber is filled, and has captured entity -> Transfurs the entity with given variant
        DRAIN(StasisChamberBlockEntity::isFilled, blockEntity -> {
            blockEntity.fluidLevelO = blockEntity.fluidLevel;
            blockEntity.fluidLevel -= 0.01f; // Take 5 seconds to drain

            blockEntity.stabilized = false;

            if (blockEntity.fluidLevel > 0.6f)
                blockEntity.ensureCapturedIsStillInside();
            else {
                SeatEntity seatEntity = blockEntity.getEntityHolder();
                if (seatEntity != null)
                    seatEntity.getPassengers().forEach(Entity::stopRiding);
            }

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
    }
}
