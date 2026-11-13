package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FilledMugBlock extends AbstractCustomShapeBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape ONE_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected final boolean diluteInWater;

    public FilledMugBlock(boolean diluteInWater) {
        super(Properties.of().offsetType(OffsetType.XZ).sound(SoundType.DECORATED_POT).strength(0.5F).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
        this.diluteInWater = diluteInWater;
    }

    public static FilledMugBlock sitsInWater() {
        return new FilledMugBlock(false);
    }

    public static FilledMugBlock dilutesInWater() {
        return new FilledMugBlock(true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    public VoxelShape shapeForState(BlockState state) {
        return ONE_AABB;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        return shapeForState(state).move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return shapeForState(state).move(vec3.x, vec3.y, vec3.z);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        if (diluteInWater && flag) {
            return ChangedBlocks.MUG.get().defaultBlockState().setValue(WATERLOGGED, true);
        }
        return super.getStateForPlacement(context).setValue(WATERLOGGED, flag);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (!(Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER && diluteInWater) {
            return SimpleWaterloggedBlock.super.placeLiquid(level, blockPos, ChangedBlocks.MUG.get().defaultBlockState(), fluidState);
        }
        return SimpleWaterloggedBlock.super.placeLiquid(level, blockPos, blockState, fluidState);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

            if (diluteInWater) {
                return ChangedBlocks.MUG.get().defaultBlockState().setValue(WATERLOGGED, true);
            }
        }

        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }
}
