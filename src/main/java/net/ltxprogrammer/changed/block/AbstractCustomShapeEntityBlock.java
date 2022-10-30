package net.ltxprogrammer.changed.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractCustomShapeEntityBlock extends BaseEntityBlock {
    protected static final Map<Block, Map<Direction, VoxelShape>> SHAPES = new HashMap<Block, Map<Direction, VoxelShape>>();
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public AbstractCustomShapeEntityBlock(BlockBehaviour.Properties properties) {
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

    protected static VoxelShape calculateShapes(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

        int times = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
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
}
