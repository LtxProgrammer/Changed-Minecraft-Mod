package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
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

public class AbstractLabDoor extends HorizontalDirectionalBlock implements NonLatexCoverableBlock {
    public static final EnumProperty<QuarterSection> SECTION = EnumProperty.create("section", QuarterSection.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final VoxelShape SHAPE_FRAME1 = Block.box(14.0D, 0.0D, 2.0D, 16.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME2 = Block.box(-16.0D, 0.0D, 2.0D, -14.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME3 = Block.box(-16.0D, 30.0D, 2.0D, 16.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME = Shapes.or(SHAPE_FRAME1, SHAPE_FRAME2, SHAPE_FRAME3);
    public static final VoxelShape SHAPE_DOOR = Block.box(-16.0D, 0.0D, 4.0D, 16.0D, 32.0D, 12.0D);
    public static final VoxelShape SHAPE_COLLISION_CLOSED = Shapes.or(SHAPE_FRAME, SHAPE_DOOR);

    private final SoundEvent open, close;

    public AbstractLabDoor(SoundEvent open, SoundEvent close) {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).strength(3.0F, 18.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, Boolean.FALSE)
                .setValue(SECTION, QuarterSection.BOTTOM_LEFT)
                .setValue(OPEN, Boolean.FALSE));
        this.open = open;
        this.close = close;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = SHAPE_FRAME;

        double x = 0.0D;
        double z = 0.0D;

        if (state.getValue(OPEN)) {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_FRAME);
            switch (state.getValue(FACING)) {
                case NORTH -> x = 1.0D;
                case EAST -> z = 1.0D;
                case SOUTH -> x = -1.0D;
                case WEST -> z = -1.0D;
            }
        }
        else {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_COLLISION_CLOSED);
            switch (state.getValue(FACING)) {
                case NORTH -> x = 1.0D;
                case EAST -> z = 1.0D;
                case SOUTH -> x = -1.0D;
                case WEST -> z = -1.0D;
            }
        }

        switch (state.getValue(SECTION)) {
            case BOTTOM_LEFT -> { return shape; }
            case TOP_LEFT -> { return shape.move(0, -1.0D, 0); }
            case TOP_RIGHT -> { return shape.move(x, -1.0D, z); }
            case BOTTOM_RIGHT -> { return shape.move(x, 0.0D, z); }
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
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            boolean place;
            switch (direction) {
                case NORTH -> place = level.getBlockState(blockpos.east()).canBeReplaced(context) && level.getBlockState(blockpos.east().above()).canBeReplaced(context);
                case EAST -> place = level.getBlockState(blockpos.south()).canBeReplaced(context) && level.getBlockState(blockpos.south().above()).canBeReplaced(context);
                case SOUTH -> place = level.getBlockState(blockpos.west()).canBeReplaced(context) && level.getBlockState(blockpos.west().above()).canBeReplaced(context);
                case WEST -> place = level.getBlockState(blockpos.north()).canBeReplaced(context) && level.getBlockState(blockpos.north().above()).canBeReplaced(context);
                default -> place = false;
            }

            if (!place) return null;
            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(OPEN, Boolean.FALSE);
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
        return state.getValue(SECTION) == QuarterSection.BOTTOM_LEFT ?
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
            case LAND, AIR -> state.getValue(OPEN);
            case WATER -> false;
        };
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(POWERED)) {
            level.playSound(null, pos, OPEN2, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (state.getValue(OPEN) && player.getDimensions(player.getPose()).makeBoundingBox(player.position()).intersects(new AABB(
                state.getValue(SECTION).getRelative(pos, state.getValue(FACING), QuarterSection.BOTTOM_LEFT),
                state.getValue(SECTION).getRelative(pos, state.getValue(FACING), QuarterSection.TOP_RIGHT))))
            return InteractionResult.FAIL;

        var wantState = !state.getValue(OPEN);
        var thisSect = state.getValue(SECTION);
        for (var sect : QuarterSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(OPEN, wantState));
            level.gameEvent(wantState ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
        level.playSound(null, pos, wantState ? open : close, SoundSource.BLOCKS, 1, 1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlockAndUpdate(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect));
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, QuarterSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, boolean allCheck) {
        if (!allCheck && state.getValue(SECTION) == QuarterSection.BOTTOM_LEFT)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheck ? Arrays.stream(QuarterSection.values()).toList() : thisSect.getOtherValues()) {
            var other = level.getBlockState(thisSect.getRelative(pos, state.getValue(FACING), sect));
            if (other.is(this) && other.getValue(SECTION) == sect)
                continue;
            return false;
        }

        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canSurvive(state, level, pos, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherBlockPos) {
        if (!this.canSurvive(state, level, pos, true))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, otherState, level, pos, otherBlockPos);
    }

    protected void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        var section = state.getValue(SECTION);
        if (section != QuarterSection.BOTTOM_LEFT) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), QuarterSection.BOTTOM_LEFT);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == QuarterSection.BOTTOM_LEFT) {
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
            } else if (state.getValue(SECTION) != QuarterSection.BOTTOM_LEFT) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }
}
