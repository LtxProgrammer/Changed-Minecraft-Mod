package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.beast.LatexBee;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class BeehiveBed extends AbstractCustomShapeBlock {
    public static final VoxelShape SHAPE_WHOLE_NORMAL = Shapes.or(
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D),
            Block.box(1.0D, 15.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    );
    public static final VoxelShape SHAPE_WHOLE_OFFSET = Block.box(1.0D, 7.0D, 1.0D, 15.0D, 12.0D, 15.0D);

    public enum BeehiveState implements StringRepresentable {
        NORMAL,
        OFFSET;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public BeehiveState getOpposite() {
            return this == NORMAL ? OFFSET : NORMAL;
        }
    }

    public static final EnumProperty<BeehiveState> STATE = EnumProperty.create("state", BeehiveState.class);
    public static final EnumProperty<BedPart> PART = BedBlock.PART;
    public static final BooleanProperty OCCUPIED = BedBlock.OCCUPIED;

    public BeehiveBed() {
        super(Properties.copy(Blocks.BEE_NEST).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never));
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(STATE, BeehiveState.NORMAL)
                .setValue(PART, BedPart.HEAD)
                .setValue(OCCUPIED, Boolean.FALSE));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(PART) == BedPart.HEAD ? super.getDrops(state, builder) : List.of();
    }

    private boolean kickBeeOutOfBed(Level level, BlockPos pos) {
        List<LatexBee> list = level.getEntitiesOfClass(LatexBee.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.get(0).stopSleeping();
            return true;
        }
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (state.getValue(PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(FACING));
                state = level.getBlockState(pos);
                if (!state.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }

            if (!canSetSpawn(level)) {
                return InteractionResult.SUCCESS;
            } else if (state.getValue(OCCUPIED)) {
                if (!this.kickBeeOutOfBed(level, pos)) {
                    player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            } else {
                player.startSleepInBed(pos).ifLeft((problem) -> {
                    if (problem != null) {
                        player.displayClientMessage(problem.getMessage(), true);
                    }

                }).ifRight(unit -> {
                    if (player instanceof ServerPlayer serverPlayer)
                        ChangedCriteriaTriggers.BEEHIVE_SLEEP.trigger(serverPlayer);
                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(STATE) == BeehiveState.NORMAL ? SHAPE_WHOLE_NORMAL : SHAPE_WHOLE_OFFSET;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STATE, PART, OCCUPIED);
    }

    public static boolean canSetSpawn(Level level) {
        return level.dimensionType().bedWorks();
    }

    @Override
    public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
            level.setBlock(blockpos, state.setValue(PART, BedPart.FOOT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Direction facing = state.getValue(FACING);
        Direction tangentA = facing.getClockWise(); // Tangent A is prioritized
        Direction tangentB = facing.getCounterClockWise();
        Level level = context.getLevel();

        BlockState stateA = level.getBlockState(pos.relative(tangentA));
        BlockState stateB = level.getBlockState(pos.relative(tangentB));

        if (stateA.is(this) && stateA.getValue(FACING).getAxis() == facing.getAxis()) {
            state = state.setValue(STATE, stateA.getValue(STATE).getOpposite());
        } else if (stateB.is(this) && stateB.getValue(FACING).getAxis() == facing.getAxis()) {
            state = state.setValue(STATE, stateB.getValue(STATE).getOpposite());
        }

        BlockPos neighborPos = pos.relative(facing.getOpposite());
        return level.getBlockState(neighborPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(neighborPos) ? state : null;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return otherState.is(this) && otherState.getValue(PART) != state.getValue(PART) ? state.setValue(OCCUPIED, otherState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, direction, otherState, level, pos, otherPos);
        }
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.HEAD ? direction.getOpposite() : direction;
    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BedPart bedpart = state.getValue(PART);
            if (bedpart == BedPart.HEAD) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return true;
    }
}
