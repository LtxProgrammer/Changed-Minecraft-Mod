package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.ServerStackBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ServerStack extends AbstractCustomShapeTallEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 30.0D, 16.0D);
    public static final VoxelShape SHAPE_DETAILED = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, 5.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 10.0D, 0.0D, 16.0D, 13.0D, 16.0D),
            Block.box(0.0D, 15.0D, 0.0D, 16.0D, 18.0D, 16.0D),
            Block.box(0.0D, 20.0D, 0.0D, 16.0D, 23.0D, 16.0D),
            Block.box(0.0D, 25.0D, 0.0D, 16.0D, 30.0D, 16.0D),
            Block.box(1.0D, 3.0D, 1.0D, 15.0D, 25.0D, 15.0D)
    );

    public ServerStack(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE : SHAPE.move(0.0D, -1.0D, 0.0D);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return getInteractionShape(blockState, level, blockPos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_DETAILED : SHAPE_DETAILED.move(0.0D, -1.0D, 0.0D);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return getInteractionShape(blockState, level, blockPos);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ServerStackBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ChangedBlockEntities.SERVER_STACK.get());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
        return typeE == typeA ? (BlockEntityTicker<A>)ticker : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends ServerStackBlockEntity> newType) {
        return level.isClientSide ? null : createTickerHelper(type, newType, ServerStackBlockEntity::serverTick);
    }
}
