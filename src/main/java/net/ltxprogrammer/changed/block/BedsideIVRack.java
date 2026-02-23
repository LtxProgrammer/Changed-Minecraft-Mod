package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BedsideIVRack extends AbstractCustomShapeTallBlock {
    public static final BooleanProperty FULL = BooleanProperty.create("full");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
    public static final VoxelShape SHAPE_STEM = Block.box(7.5D, 4.0D, 7.5D, 8.5D, 25.0D, 8.5D);
    public static final VoxelShape SHAPE_TOP = Block.box(2.5D, 25.0D, 7.5D, 13.5D, 30.0D, 8.5D);
    public static final VoxelShape SHAPE_WHOLE = Shapes.or(SHAPE_BASE, SHAPE_STEM, SHAPE_TOP);

    public BedsideIVRack() {
        super(BlockBehaviour.Properties.of().sound(SoundType.METAL).dynamicShape().strength(3.0F, 5.0F).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, false).setValue(FULL, false));
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, state.getValue(FACING)), 3);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state) {
        super.createBlockStateDefinition(state);
        state.add(FULL);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        VoxelShape shape = calculateShapes(p_60547_.getValue(FACING), SHAPE_WHOLE);

        switch (p_60547_.getValue(HALF)) {
            case LOWER -> { return shape; }
            case UPPER -> { return shape.move(0, -1.0D, 0); }
        }

        return shape;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }
}
