package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractCustomShapeBlock extends Block {
    protected static final Map<Block, Map<Direction, VoxelShape>> SHAPES = new HashMap<Block, Map<Direction, VoxelShape>>();
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public AbstractCustomShapeBlock(BlockBehaviour.Properties properties) {
        super(properties.dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        return new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    protected static final Map<Direction, Map<VoxelShape, VoxelShape>> SHAPE_CACHE = new HashMap<>();
    private static final AtomicBoolean FLAG_CALCULATING_SHAPES = new AtomicBoolean(false);
    protected static VoxelShape calculateShapes(Direction to, VoxelShape shape) {
        while (FLAG_CALCULATING_SHAPES.compareAndExchange(false, true));

        VoxelShape rShape = SHAPE_CACHE.computeIfAbsent(to, direction -> new HashMap<>())
                .computeIfAbsent(shape, voxelShape -> {
            VoxelShape[] buffer = new VoxelShape[] { voxelShape, Shapes.empty() };

            int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
            for (int i = 0; i < times; i++) {
                buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                        Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
                buffer[0] = buffer[1];
                buffer[1] = Shapes.empty();
            }

            return buffer[0];
        });

        FLAG_CALCULATING_SHAPES.set(false);
        return rShape;
    }

    protected void runCalculation(VoxelShape shape) {
        SHAPES.put(this, new HashMap<Direction, VoxelShape>());
        Map<Direction, VoxelShape> facingMap = SHAPES.get(this);
        for (Direction direction : Direction.values()) {
            facingMap.put(direction, calculateShapes(direction, shape));
        }
    }

    protected VoxelShape transformShape(VoxelShape shape, double x, double y, double z) {
        AtomicReference<VoxelShape> newShape = new AtomicReference<>();
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            newShape.set(Shapes.or(Block.box(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z),
                    newShape.get() == null ? Shapes.empty() : newShape.get()));
        });
        return newShape.get();
    }

    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.empty();
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return switch (direction) {
            case CLOCKWISE_90 -> state.setValue(FACING, state.getValue(FACING).getClockWise());
            case CLOCKWISE_180 -> state.setValue(FACING, state.getValue(FACING).getOpposite());
            case COUNTERCLOCKWISE_90 -> state.setValue(FACING, state.getValue(FACING).getCounterClockWise());
            default -> state;
        };
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case FRONT_BACK -> state.getValue(FACING).getAxis() == Direction.Axis.Z ? state.setValue(FACING, state.getValue(FACING).getOpposite()) : state;
            case LEFT_RIGHT -> state.getValue(FACING).getAxis() == Direction.Axis.X ? state.setValue(FACING, state.getValue(FACING).getOpposite()) : state;
            default -> state;
        };
    }
}
