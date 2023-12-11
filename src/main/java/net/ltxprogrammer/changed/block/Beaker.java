package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.item.AbstractLatexGoo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class Beaker extends Block implements SimpleWaterloggedBlock, NonLatexCoverableBlock {
    public static final EnumProperty<LatexType> FILLED = EnumProperty.create("filled", LatexType.class);
    public static final VoxelShape AABB = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 11.0D, 12.5D);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public Beaker() {
        super(Properties.of(Material.GLASS).sound(SoundType.GLASS).instabreak().dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(FILLED, LatexType.NEUTRAL).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED, WATERLOGGED);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(ChangedBlocks.BEAKER.get()));
        var type = state.getValue(FILLED);
        if (type == LatexType.NEUTRAL)
            return items;
        else {
            items.add(new ItemStack(type.goo.get()));
            return items;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var itemInHand = player.getItemInHand(hand);
        if (state.getValue(FILLED) == LatexType.NEUTRAL && itemInHand.getItem() instanceof AbstractLatexGoo goo) {
            if (!player.isCreative())
                itemInHand.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(FILLED, goo.getLatexType()));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        else if (state.getValue(FILLED) != LatexType.NEUTRAL) {
            if (!player.isCreative())
                player.addItem(new ItemStack(state.getValue(FILLED).goo.get()));
            level.setBlockAndUpdate(pos, state.setValue(FILLED, LatexType.NEUTRAL));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        return AABB.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return AABB.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
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
