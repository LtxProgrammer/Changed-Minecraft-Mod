package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.block.entity.LabDoorOpenerEntity;
import net.ltxprogrammer.changed.block.entity.OpenableDoor;
import net.ltxprogrammer.changed.block.entity.PurifierBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.ltxprogrammer.changed.init.ChangedSounds.OPEN2;

public class AbstractLargeLabDoor extends HorizontalDirectionalBlock implements NonLatexCoverableBlock, EntityBlock, OpenableDoor {
    public static final EnumProperty<NineSection> SECTION = EnumProperty.create("section", NineSection.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final VoxelShape SHAPE_FRAME1 = Block.box(30.0D, 0.0D, 2.0D, 32.0D, 48.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME2 = Block.box(-16.0D, 0.0D, 2.0D, -14.0D, 48.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME3 = Block.box(-16.0D, 46.0D, 2.0D, 32.0D, 48.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME = Shapes.or(SHAPE_FRAME1, SHAPE_FRAME2, SHAPE_FRAME3);
    public static final VoxelShape SHAPE_DOOR = Block.box(-16.0D, 0.0D, 4.0D, 32.0D, 48.0D, 12.0D);
    public static final VoxelShape SHAPE_DOOR_SLIM = Block.box(-16.0D, 0.0D, 7.0D, 32.0D, 48.0D, 9.0D);
    public static final VoxelShape SHAPE_COLLISION_CLOSED = Shapes.or(SHAPE_FRAME, SHAPE_DOOR);
    public static final VoxelShape SHAPE_COLLISION_CLOSED_SLIM = Shapes.or(SHAPE_FRAME, SHAPE_DOOR_SLIM);

    private final SoundEvent open, close;

    private final VoxelShape shapeFrame;
    private final VoxelShape shapeCollisionClosed;

    public AbstractLargeLabDoor(SoundEvent open, SoundEvent close, boolean slim) {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 9.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, Boolean.FALSE)
                .setValue(SECTION, NineSection.BOTTOM_MIDDLE)
                .setValue(OPEN, Boolean.FALSE));
        this.open = open;
        this.close = close;

        this.shapeFrame = SHAPE_FRAME;
        this.shapeCollisionClosed = slim ? SHAPE_COLLISION_CLOSED_SLIM : SHAPE_COLLISION_CLOSED;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = shapeFrame;

        double x = 0.0D;
        double z = 0.0D;

        if (state.getValue(OPEN)) {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), shapeFrame);
            switch (state.getValue(FACING)) {
                case NORTH -> x = 1.0D;
                case EAST -> z = 1.0D;
                case SOUTH -> x = -1.0D;
                case WEST -> z = -1.0D;
            }
        }
        else {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), shapeCollisionClosed);
            switch (state.getValue(FACING)) {
                case NORTH -> x = 1.0D;
                case EAST -> z = 1.0D;
                case SOUTH -> x = -1.0D;
                case WEST -> z = -1.0D;
            }
        }

        switch (state.getValue(SECTION)) {
            case BOTTOM_LEFT -> { return shape.move(-x, 0.0D, -z); }
            case MIDDLE_LEFT -> { return shape.move(-x, -1.0D, -z); }
            case TOP_LEFT -> { return shape.move(-x, -2.0D, -z); }

            case BOTTOM_MIDDLE -> { return shape.move(0, 0.0D, 0); }
            case CENTER -> { return shape.move(0, -1.0D, 0); }
            case TOP_MIDDLE -> { return shape.move(0, -2.0D, 0); }

            case BOTTOM_RIGHT -> { return shape.move(x, 0.0D, z); }
            case MIDDLE_RIGHT -> { return shape.move(x, -1.0D, z); }
            case TOP_RIGHT -> { return shape.move(x, -2.0D, z); }
        }

        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, POWERED, SECTION, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxBuildHeight() - 2) {
            for (var sect : NineSection.BOTTOM_MIDDLE.getOtherValues()) {
                if (!level.getBlockState(NineSection.BOTTOM_MIDDLE.getRelative(blockpos, direction.getOpposite(), sect)).canBeReplaced(context))
                    return null;
            }

            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(OPEN, Boolean.FALSE).setValue(SECTION, NineSection.BOTTOM_MIDDLE);
        } else {
            return null;
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState otherState, boolean flag) {
        if (!otherState.is(state.getBlock())) {
            checkIfPowered(state, level, pos);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(SECTION) == NineSection.BOTTOM_MIDDLE ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    public boolean wantPowered(BlockState state, Level level, BlockPos pos) {
        if (level.getBestNeighborSignal(pos) > 0)
            return true;

        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues()) {
            if (level.getBestNeighborSignal(thisSect.getRelative(pos, state.getValue(FACING), sect)) > 0)
                return true;
        }

        return false;
    }

    public void checkIfPowered(BlockState state, Level level, BlockPos pos) {
        boolean wantOn = wantPowered(state, level, pos);
        if (wantOn != state.getValue(POWERED)) {
            if (!wantOn && state.getValue(OPEN)) {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, Boolean.FALSE).setValue(OPEN, Boolean.FALSE));
                if (state.getValue(SECTION) == NineSection.CENTER)
                    level.playSound(null, pos, close, SoundSource.BLOCKS, 1, 1);
                level.gameEvent(GameEvent.BLOCK_CLOSE, pos);
            } else
                level.setBlockAndUpdate(pos, state.setValue(POWERED, wantOn));
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block source, BlockPos sourcePos, boolean flag) {
        checkIfPowered(state, level, pos);
        super.neighborChanged(state, level, pos, source, sourcePos, flag);
    }

    public boolean isPathfindable(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return switch (type) {
            case LAND, AIR -> state.getValue(POWERED);
            case WATER -> false;
        };
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(POWERED)) {
            level.playSound(null, pos, OPEN2, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlockAndUpdate(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect));
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, NineSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction.Axis> allCheckOrAxis) {
        if (allCheckOrAxis.left().isPresent() && !allCheckOrAxis.left().get() && state.getValue(SECTION) == NineSection.BOTTOM_MIDDLE)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrAxis.left().isPresent() && allCheckOrAxis.left().get() ? Arrays.stream(NineSection.values()).toList() : thisSect.getOtherValues()) {
            if (allCheckOrAxis.right().isPresent()) {
                if (!thisSect.isOnAxis(sect, state.getValue(FACING), allCheckOrAxis.right().get()))
                    continue;
            }

            var other = level.getBlockState(thisSect.getRelative(pos, state.getValue(FACING), sect));
            if (other.is(this) && other.getValue(SECTION) == sect)
                continue;
            return false;
        }

        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canSurvive(state, level, pos, Either.left(false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherBlockPos) {
        if (!this.canSurvive(state, level, pos, Either.right(direction.getAxis())))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, otherState, level, pos, otherBlockPos);
    }

    protected void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        var section = state.getValue(SECTION);
        if (section != NineSection.BOTTOM_MIDDLE) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), NineSection.BOTTOM_MIDDLE);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == NineSection.BOTTOM_MIDDLE) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventCreativeDropFromBottomPart(level, pos, state, player);
            } else if (state.getValue(SECTION) != NineSection.BOTTOM_MIDDLE) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return super.mirror(state, mirror);
        else {
            return super.mirror(state, mirror).setValue(SECTION, state.getValue(SECTION).getHorizontalNeighbor());
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(SECTION) != NineSection.BOTTOM_MIDDLE)
            return null;
        return new LabDoorOpenerEntity(pos, state, this);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ChangedBlockEntities.LAB_DOOR_OPENER.get());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
        return typeE == typeA ? (BlockEntityTicker<A>)ticker : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends LabDoorOpenerEntity> newType) {
        return level.isClientSide ? null : createTickerHelper(type, newType, LabDoorOpenerEntity::serverTick);
    }

    @Override
    public boolean openDoor(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(OPEN))
            return false;

        var wantState = true;
        var thisSect = state.getValue(SECTION);
        for (var sect : NineSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(OPEN, wantState));
            level.gameEvent(GameEvent.BLOCK_OPEN, pos);
        }
        level.playSound(null, pos, open, SoundSource.BLOCKS, 1, 1);
        return true;
    }

    @Override
    public boolean closeDoor(BlockState state, Level level, BlockPos pos) {
        if (!state.getValue(OPEN))
            return false;

        var wantState = false;
        var thisSect = state.getValue(SECTION);
        for (var sect : NineSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(OPEN, wantState));
            level.gameEvent(GameEvent.BLOCK_CLOSE, pos);
        }
        level.playSound(null, pos, close, SoundSource.BLOCKS, 1, 1);
        return true;
    }

    @Override
    public boolean isOpen(BlockState state, Level level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    @Override
    public AABB getDetectionSize(BlockState state, Level level, BlockPos pos) {
        var thisSect = state.getValue(SECTION);

        var facing = state.getValue(FACING);
        var aabbBottomLeft = new AABB(thisSect.getRelative(pos, facing, NineSection.BOTTOM_LEFT));
        var aabbTopRight = new AABB(thisSect.getRelative(pos, facing, NineSection.TOP_RIGHT));
        var aabb = aabbBottomLeft.minmax(aabbTopRight);

        return aabb.inflate(facing.getAxis() == Direction.Axis.X ? 1.0 : 0.0, 0.0, facing.getAxis() == Direction.Axis.Z ? 1.0 : 0.0);
    }
}
