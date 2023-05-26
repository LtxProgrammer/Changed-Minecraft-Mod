package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.PlayerMoverInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class DuctBlock extends ChangedBlock
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty VENTED = BooleanProperty.create("vented");
    public static final BooleanProperty[] FACES = { NORTH, EAST, SOUTH, WEST, UP, DOWN };
    public static final Map<Direction, BooleanProperty> BY_DIRECTION = Map.of(
            Direction.UP, UP, Direction.DOWN, DOWN, Direction.NORTH, NORTH, Direction.SOUTH, SOUTH, Direction.EAST, EAST, Direction.WEST, WEST);
    private static final VoxelShape SHAPE_FRAME = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0),
            Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 2.0),
            Block.box(0.0, 0.0, 14.0, 2.0, 16.0, 16.0),
            Block.box(14.0, 0.0, 14.0, 16.0, 16.0, 16.0),

            Block.box(2.0, 0.0, 0.0, 14.0, 2.0, 2.0),
            Block.box(2.0, 14.0, 0.0, 14.0, 16.0, 2.0),
            Block.box(2.0, 0.0, 14.0, 14.0, 2.0, 16.0),
            Block.box(2.0, 14.0, 14.0, 14.0, 16.0, 16.0),

            Block.box(0.0, 0.0, 2.0, 2.0, 2.0, 14.0),
            Block.box(0.0, 14.0, 2.0, 2.0, 16.0, 14.0),
            Block.box(14.0, 0.0, 2.0, 16.0, 2.0, 14.0),
            Block.box(14.0, 14.0, 2.0, 16.0, 16.0, 14.0));
    private static final Map<Direction.Axis, VoxelShape> SHAPE_DUCT = Map.of(
            Direction.Axis.Y, Shapes.or(
                    Block.box(2.0, 0.0, 2.0, 2.02, 16.0, 14.0),
                    Block.box(2.0, 0.0, 13.98, 14.0, 16.0, 14.0),
                    Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 2.02),
                    Block.box(13.98, 0.0, 2.0, 14.0, 16.0, 14.0)),
            Direction.Axis.X, Shapes.or(
                    Block.box(0.0, 2.0, 2.0, 16.0, 14.0, 2.02),
                    Block.box(0.0, 2.0, 13.98, 16.0, 14.0, 14.0),
                    Block.box(0.0, 2.0, 2.0, 16.0, 2.02, 14.0),
                    Block.box(0.0, 13.98, 2.0, 16.0, 14.0, 14.0)),
            Direction.Axis.Z, Shapes.or(
                    Block.box(2.0, 2.0, 0.0, 2.02, 14.0, 16.0),
                    Block.box(13.98, 2.0, 0.0, 14.0, 14.0, 16.0),
                    Block.box(2.0, 2.0, 0.0, 14.0, 2.02, 16.0),
                    Block.box(2.0, 13.98, 0.0, 14.0, 14.0, 16.0)));
    private static final Map<Direction, VoxelShape> SHAPE_DUCT_PANEL = Map.of(
            Direction.UP, Block.box(2.0, 14.0, 2.0, 14.0, 16.0, 14.0),
            Direction.DOWN, Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Direction.NORTH, Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 2.0),
            Direction.SOUTH, Block.box(2.0, 2.0, 14.0, 14.0, 14.0, 16.0),
            Direction.EAST, Block.box(14.0, 2.0, 2.0, 16.0, 14.0, 14.0),
            Direction.WEST, Block.box(0.0, 2.0, 2.0, 2.0, 14.0, 14.0));
    private final Map<BlockState, VoxelShape> COMPUTED_SHAPES = new HashMap<>();

    public DuctBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(VENTED, false));
        this.getStateDefinition().getPossibleStates().forEach(state -> this.COMPUTED_SHAPES.computeIfAbsent(state, this::computeShape));
    }

    protected static Optional<Direction.Axis> nonJointedAxis(final BlockState blockState) {
        Optional<Direction.Axis> candidate = Optional.empty();
        for (final Direction.Axis axis : Direction.Axis.values()) {
            if (blockState.getValue(BY_DIRECTION.get(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE))) &&
                    blockState.getValue(BY_DIRECTION.get(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE))))
                candidate = Optional.of(axis);
        }

        for (final Direction dir : Direction.values()) {
            if (blockState.getValue(BY_DIRECTION.get(dir)) && candidate.isPresent() && dir.getAxis() != candidate.get())
                return Optional.empty();
        }

        return candidate;
    }

    protected BlockState getWantedState(final BlockState current, final BlockPos blockPos, final BlockGetter level) {
        final AtomicReference<BlockState> wanted = new AtomicReference<>(current);
        for (final Direction direction : Direction.values()) {
            if (level.getBlockState(blockPos.relative(direction)).is(ChangedTags.Blocks.DUCT_CONNECTOR)) {
                wanted.set(wanted.get().setValue(BY_DIRECTION.get(direction), true));
            }
        }
        nonJointedAxis(wanted.get()).ifPresent(axis -> {
            try {
                BlockState positive = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)));
                BlockState negative = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE)));
                if (positive.is(this) && negative.is(this))
                    if (!positive.getValue(VENTED) && !negative.getValue(VENTED))
                        wanted.set(wanted.get().setValue(VENTED, true));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return wanted.get();
    }

    @NotNull
    public VoxelShape getCollisionShape(final BlockState blockState, final BlockGetter level, final BlockPos blockPos, final CollisionContext context) {
        final VoxelShape shape = this.COMPUTED_SHAPES.get(blockState);
        if (shape == null) {
            throw new IllegalStateException("Undefined state shape");
        }
        return shape;
    }

    @NotNull
    public VoxelShape getInteractionShape(final BlockState blockState, final BlockGetter level, final BlockPos blockPos) {
        final VoxelShape shape = this.COMPUTED_SHAPES.get(blockState);
        if (shape == null)
            throw new IllegalStateException("Undefined state shape");
        return shape;
    }

    @NotNull
    public VoxelShape getShape(final BlockState blockState, final BlockGetter level, final BlockPos blockPos, final CollisionContext context) {
        final VoxelShape shape = this.COMPUTED_SHAPES.get(blockState);
        if (shape == null)
            throw new IllegalStateException("Undefined state shape");
        return shape;
    }

    public VoxelShape computeShape(final BlockState blockState) {
        final Optional<Direction.Axis> opt = nonJointedAxis(blockState);
        if (opt.isEmpty()) {
            VoxelShape shape = SHAPE_FRAME;
            for (var dir : Direction.values())
                if (!blockState.getValue(BY_DIRECTION.get(dir)))
                    shape = Shapes.or(shape, SHAPE_DUCT_PANEL.get(dir));
            return shape;
        }
        return SHAPE_DUCT.get(opt.get());
    }

    public boolean isLadder(final BlockState state, final LevelReader level, final BlockPos pos, final LivingEntity entity) {
        BlockPos entityBlockPos = new BlockPos(entity.position().add(0.0, 0.25, 0.0));
        return super.isLadder(state, level, pos, entity) || entityBlockPos.equals(pos) || entity.eyeBlockPosition().equals(pos);
    }

    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.getWantedState(super.getStateForPlacement(context), context.getClickedPos(), context.getLevel());
    }

    public BlockState updateShape(final BlockState blockState, final Direction direction, final BlockState blockStateOther, final LevelAccessor level, final BlockPos blockPos, final BlockPos blockPosOther) {
        BlockState wanted = blockState.setValue(BY_DIRECTION.get(direction), blockStateOther.is(ChangedTags.Blocks.DUCT_CONNECTOR));
        final Optional<Direction.Axis> opt = nonJointedAxis(wanted);
        if (opt.isPresent()) {
            final BlockState positive = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(opt.get(), Direction.AxisDirection.POSITIVE)));
            final BlockState negative = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(opt.get(), Direction.AxisDirection.NEGATIVE)));
            if (!positive.is(this) || !negative.is(this)) {
                return wanted;
            }
            if (!positive.getValue(VENTED) && !negative.getValue(VENTED)) {
                wanted = wanted.setValue(VENTED, true);
            }
        }
        return wanted;
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, VENTED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var direction = hitResult.getDirection(); // TODO isn't the face of the block the player clicked, need to find a better version
        if (!state.getValue(BY_DIRECTION.get(direction)))
            return super.use(state, level, blockPos, player, hand, hitResult);
        if (!level.getBlockState(blockPos.relative(direction)).is(ChangedTags.Blocks.DUCT_EXIT))
            return super.use(state, level, blockPos, player, hand, hitResult);

        if (player instanceof PlayerDataExtension playerDataExtension && !ProcessTransfur.isPlayerOrganic(player)
                && playerDataExtension.getPlayerMover() == null) {
            var instance = PlayerMover.DUCT_MOVER.get().createInstance();
            instance.ductBlock = this;
            player.moveTo(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

            playerDataExtension.setPlayerMover(instance);
        }

        return super.use(state, level, blockPos, player, hand, hitResult);
    }

    public static class DuctMover extends PlayerMover<DuctMover.DuctMoverInstance> {
        public static class DuctMoverInstance extends PlayerMoverInstance<DuctMover> {
            public Block ductBlock;
            public int coolDown = 0;

            public DuctMoverInstance(DuctMover parent) {
                super(parent);
            }

            public static Direction.Axis getClosestAxis(Vec3 vec3) {
                if (Math.abs(vec3.x) > Math.abs(vec3.y) && Math.abs(vec3.x) > Math.abs(vec3.z))
                    return Direction.Axis.X;
                if (Math.abs(vec3.y) > Math.abs(vec3.x) && Math.abs(vec3.y) > Math.abs(vec3.z))
                    return Direction.Axis.Y;
                else
                    return Direction.Axis.Z;
            }

            public static Direction getClosestDirection(Vec3 vec3) {
                return switch (getClosestAxis(vec3)) {
                    case X -> Direction.fromAxisAndDirection(Direction.Axis.X, vec3.x > 0.0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
                    case Y -> Direction.fromAxisAndDirection(Direction.Axis.Y, vec3.y > 0.0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
                    case Z -> Direction.fromAxisAndDirection(Direction.Axis.Z, vec3.z > 0.0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
                };
            }

            public static void playDuctSound(BlockPos where) {

            }

            @Override
            public void saveTo(CompoundTag tag) {
                super.saveTo(tag);
                tag.putInt("block", Registry.BLOCK.getId(ductBlock));
            }

            @Override
            public void readFrom(CompoundTag tag) {
                super.readFrom(tag);
                this.ductBlock = Registry.BLOCK.byId(tag.getInt("block"));
            }

            @Override
            public void aiStep(Player player, InputWrapper input, LogicalSide side) {
                player.setDeltaMovement(0, 0, 0);
                player.refreshDimensions();
                player.noPhysics = true;

                if (coolDown > 0) {
                    coolDown--;
                    return;
                }

                Vec3 lookAngle = player.getLookAngle();
                Vec3 upAngle = player.getUpVector(0.5f);

                Direction lookDir = getClosestDirection(lookAngle);
                Direction upDir = getClosestDirection(upAngle);
                Direction leftDir = getClosestDirection(upAngle.cross(lookAngle));

                Direction moveDir = null;
                if (Math.abs(input.getForwardImpulse()) > 1.0E-5F)
                    moveDir = input.getForwardImpulse() > 0.0f ? lookDir : lookDir.getOpposite();
                else if (Math.abs(input.getLeftImpulse()) > 1.0E-5F)
                    moveDir = input.getLeftImpulse() > 0.0f ? leftDir : leftDir.getOpposite();

                if (moveDir == null) return;

                BlockPos currentPos = player.blockPosition();
                BlockPos nextPos = currentPos.relative(moveDir);

                BlockState nextState = player.level.getBlockState(nextPos);
                if (!nextState.is(ductBlock) && !nextState.is(ChangedTags.Blocks.DUCT_EXIT))
                    return;

                player.moveTo(nextPos.getX() + 0.5, nextPos.getY() + 0.5, nextPos.getZ() + 0.5);
                player.xOld = currentPos.getX() + 0.5;
                player.yOld = currentPos.getY() + 0.5;
                player.zOld = currentPos.getZ() + 0.5;
                player.xo = currentPos.getX() + 0.5;
                player.yo = currentPos.getY() + 0.5;
                player.zo = currentPos.getZ() + 0.5;
                playDuctSound(nextPos);

                coolDown = 5;
            }

            @Override
            public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {

            }

            @Override
            public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
                return !player.level.getBlockState(player.blockPosition()).is(ductBlock);
            }

            @Override
            public EntityDimensions getDimensions(Pose pose, EntityDimensions currentDimensions) {
                return EntityDimensions.scalable(0.5f, 0.5f);
            }

            @Override
            public float getEyeHeight(Pose pose, float eyeHeight) {
                return 0.25f;
            }
        }

        @Override
        public DuctMoverInstance createInstance() {
            return new DuctMoverInstance(this);
        }
    }
}