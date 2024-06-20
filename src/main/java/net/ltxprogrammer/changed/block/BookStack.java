package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class BookStack extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty BOOKS = IntegerProperty.create("books", 1, 8);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape ONE_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 2.0D, 12.5D);
    public static final VoxelShape TWO_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 4.0D, 12.5D);
    public static final VoxelShape THREE_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 6.0D, 12.5D);
    public static final VoxelShape FOUR_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 8.0D, 12.5D);
    public static final VoxelShape FIVE_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 10.0D, 12.5D);
    public static final VoxelShape SIX_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 12.0D, 12.5D);
    public static final VoxelShape SEVEN_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 14.0D, 12.5D);
    public static final VoxelShape EIGHT_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 16.0D, 12.5D);

    public BookStack(Properties properties) {
        super(properties.dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(BOOKS, 1).setValue(WATERLOGGED, false));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of(new ItemStack(ChangedItems.LAB_BOOK.get(), state.getValue(BOOKS)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BOOKS, WATERLOGGED);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        super.setPlacedBy(p_52749_, p_52750_, p_52751_, p_52752_, p_52753_);
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        return super.getRenderShape(p_54559_);
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        return (switch (state.getValue(BOOKS)) {
            case 1 -> ONE_AABB;
            case 2 -> TWO_AABB;
            case 3 -> THREE_AABB;
            case 4 -> FOUR_AABB;
            case 5 -> FIVE_AABB;
            case 6 -> SIX_AABB;
            case 7 -> SEVEN_AABB;
            case 8 -> EIGHT_AABB;
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(BOOKS));
        }).move(vec3.x, vec3.y, vec3.z);
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var belowState = level.getBlockState(pos.below());
        if (belowState.is(this) && belowState.getValue(BOOKS) == 8)
            return true;
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().getItem() == this.asItem() && state.getValue(BOOKS) < 8 || super.canBeReplaced(state, context);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.cycle(BOOKS);
        } else {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;
            return super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(flag));
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }
}
