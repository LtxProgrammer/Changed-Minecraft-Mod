package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.BedsideIVRackBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BedsideIVRack extends AbstractCustomShapeTallEntityBlock {
    public static final BooleanProperty FULL = BooleanProperty.create("full");

    public static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
    public static final VoxelShape SHAPE_STEM = Block.box(7.5D, 4.0D, 7.5D, 8.5D, 27.0D, 8.5D);
    public static final VoxelShape SHAPE_TOP = Block.box(2.0D, 27.0D, 7.5D, 14.0D, 32.0D, 8.5D);
    public static final VoxelShape SHAPE_WHOLE = Shapes.or(SHAPE_BASE, SHAPE_STEM, SHAPE_TOP);

    public BedsideIVRack() {
        super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).dynamicShape().strength(3.0F, 5.0F).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FULL, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153277_, BlockState p_153278_) {
        return new BedsideIVRackBlockEntity(p_153277_, p_153278_);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return true;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_52863_) {
        BlockPos blockpos = p_52863_.getClickedPos();
        Level level = p_52863_.getLevel();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(p_52863_) ? super.getStateForPlacement(p_52863_) : null;
    }

    public void setPlacedBy(Level p_52872_, BlockPos p_52873_, BlockState p_52874_, LivingEntity p_52875_, ItemStack p_52876_) {
        BlockPos blockpos = p_52873_.above();
        p_52872_.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, p_52874_.getValue(FACING)), 3);
    }

    public boolean canSurvive(BlockState p_52887_, LevelReader p_52888_, BlockPos p_52889_) {
        if (p_52887_.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(p_52887_, p_52888_, p_52889_);
        } else {
            BlockState blockstate = p_52888_.getBlockState(p_52889_.below());
            if (p_52887_.getBlock() != this) return super.canSurvive(p_52887_, p_52888_, p_52889_); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    public static void placeAt(LevelAccessor p_153174_, BlockState p_153175_, BlockPos p_153176_, int p_153177_) {
        BlockPos blockpos = p_153176_.above();
        p_153174_.setBlock(p_153176_, p_153175_.setValue(HALF, DoubleBlockHalf.LOWER), p_153177_);
        p_153174_.setBlock(blockpos, p_153175_.setValue(HALF, DoubleBlockHalf.UPPER), p_153177_);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.BLOCK;
    }

    public void playerWillDestroy(Level p_52878_, BlockPos p_52879_, BlockState p_52880_, Player p_52881_) {
        if (!p_52878_.isClientSide) {
            if (p_52881_.isCreative()) {
                preventCreativeDropFromBottomPart(p_52878_, p_52879_, p_52880_, p_52881_);
            } else {
                dropResources(p_52880_, p_52878_, p_52879_, (BlockEntity)null, p_52881_, p_52881_.getMainHandItem());
            }
        }

        switch (p_52880_.getValue(HALF)) {
            case LOWER -> p_52878_.setBlock(p_52879_.above(), Blocks.AIR.defaultBlockState(), 3);
            case UPPER -> p_52878_.setBlock(p_52879_.below(), Blocks.AIR.defaultBlockState(), 3);
        }

        super.playerWillDestroy(p_52878_, p_52879_, p_52880_, p_52881_);
    }

    public void playerDestroy(Level p_52865_, Player p_52866_, BlockPos p_52867_, BlockState p_52868_, @Nullable BlockEntity p_52869_, ItemStack p_52870_) {
        super.playerDestroy(p_52865_, p_52866_, p_52867_, Blocks.AIR.defaultBlockState(), p_52869_, p_52870_);
    }

    protected static void preventCreativeDropFromBottomPart(Level p_52904_, BlockPos p_52905_, BlockState p_52906_, Player p_52907_) {
        DoubleBlockHalf doubleblockhalf = p_52906_.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = p_52905_.below();
            BlockState blockstate = p_52904_.getBlockState(blockpos);
            if (blockstate.is(p_52906_.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                p_52904_.setBlock(blockpos, blockstate1, 35);
                p_52904_.levelEvent(p_52907_, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52901_) {
        super.createBlockStateDefinition(p_52901_);
        p_52901_.add(FULL);
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        if (p_54559_.getValue(HALF) == DoubleBlockHalf.LOWER)
            return RenderShape.MODEL;
        else
            return RenderShape.INVISIBLE;
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

    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (!state.getValue(FULL) && stack.is(ChangedItems.LATEX_SYRINGE.get())) {
            BlockPos other = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockEntity blockEntity = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : other);
            if (blockEntity instanceof BedsideIVRackBlockEntity bedsideIVRackBlockEntity) {
                bedsideIVRackBlockEntity.items.set(0, stack);
                player.setItemInHand(hand, new ItemStack(ChangedItems.SYRINGE.get()));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153273_, BlockState p_153274_, BlockEntityType<T> p_153275_) {
        return p_153273_.isClientSide ? null : createTickerHelper(p_153275_, ChangedBlockEntities.BEDSIDE_IV_RACK.get(), BedsideIVRackBlockEntity::serverTick);
    }
}
