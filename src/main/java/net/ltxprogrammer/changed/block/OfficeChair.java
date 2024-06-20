package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.OfficeChairBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class OfficeChair extends BaseEntityBlock implements PartialEntityBlock, SeatableBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SHAPE = Block.box(3.5D, 0.0D, 3.5D, 12.5D, 9.0D, 12.5D);

    public OfficeChair() {
        super(Properties.of(Material.WOOL).strength(1.0F).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never)
                .sound(SoundType.METAL));
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE : Shapes.empty();
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(ROTATION, state.getValue(ROTATION)), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context) ?
                this.defaultBlockState().setValue(ROTATION, Mth.floor((double) (context.getRotation() * 16.0F / 360.0F) + 0.5D) & 15) : null;
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, level, pos);
        } else {
            BlockState blockstate = level.getBlockState(pos.below());
            if (state.getBlock() != this) return super.canSurvive(state, level, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, HALF);
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getValue(HALF) != DoubleBlockHalf.UPPER && super.canDropFromExplosion(state, level, pos, explosion);
    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                AbstractCustomShapeTallBlock.preventCreativeDropFromBottomPart(level, pos, state, player);
            }
        }

        switch (state.getValue(HALF)) {
            case LOWER -> level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
            case UPPER -> level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 3);
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    private static final Vec3 SIT_OFFSET = new Vec3(0.0D, 12.5D / 16.0D - 1.0D, 0.0D);

    @Override
    public Vec3 getSitOffset(BlockGetter level, BlockState state, BlockPos pos) {
        return SIT_OFFSET;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OfficeChairBlockEntity(pos, state);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return state.getValue(HALF).equals(DoubleBlockHalf.UPPER) ? null :
                createTickerHelper(type, ChangedBlockEntities.OFFICE_CHAIR.get(), OfficeChairBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.getBlockEntity(pos) instanceof OfficeChairBlockEntity blockEntity) {
            return blockEntity.sitEntity(player) ?
                    InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }
}
