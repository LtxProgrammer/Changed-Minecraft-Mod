package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class DroppedOrange extends Block implements NonLatexCoverableBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty ORANGES = IntegerProperty.create("oranges", 1, 8);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape ONE_AABB = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 2.0D, 9.5D);
    protected static final VoxelShape TWO_AABB = Block.box(4.5D, 0.0D, 4.5D, 11.5D, 2.0D, 11.5D);
    protected static final VoxelShape THREE_FOUR_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
    protected static final VoxelShape FIVE_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 2.0D, 12.5D);
    protected static final VoxelShape SIXPLUS_AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 4.0D, 12.5D);

    public DroppedOrange() {
        super(BlockBehaviour.Properties.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE).sound(SoundType.WART_BLOCK).dynamicShape().instabreak());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORANGES, WATERLOGGED);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of(new ItemStack(ChangedItems.ORANGE.get(), state.getValue(ORANGES)));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        switch (state.getValue(ORANGES)) {
            case 1:
            default:
                return ONE_AABB.move(vec3.x, vec3.y, vec3.z);
            case 2:
                return TWO_AABB.move(vec3.x, vec3.y, vec3.z);
            case 3, 4:
                return THREE_FOUR_AABB.move(vec3.x, vec3.y, vec3.z);
            case 5:
                return FIVE_AABB.move(vec3.x, vec3.y, vec3.z);
            case 6, 7, 8:
                return SIXPLUS_AABB.move(vec3.x, vec3.y, vec3.z);
        }
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = blockState.getOffset(blockGetter, pos);
        switch (blockState.getValue(ORANGES)) {
            case 1:
            default:
                return ONE_AABB.move(vec3.x, vec3.y, vec3.z);
            case 2:
                return TWO_AABB.move(vec3.x, vec3.y, vec3.z);
            case 3, 4:
                return THREE_FOUR_AABB.move(vec3.x, vec3.y, vec3.z);
            case 5:
                return FIVE_AABB.move(vec3.x, vec3.y, vec3.z);
            case 6, 7, 8:
                return SIXPLUS_AABB.move(vec3.x, vec3.y, vec3.z);
        }
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().getItem() == this.asItem() && state.getValue(ORANGES) < 8 || super.canBeReplaced(state, context);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.cycle(ORANGES);
        } else {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;
            return super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(flag));
        }
    }
}
