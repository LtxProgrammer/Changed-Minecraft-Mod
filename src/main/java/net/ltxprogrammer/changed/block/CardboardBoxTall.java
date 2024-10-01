package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.CardboardBoxTallBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CardboardBoxTall extends AbstractCustomShapeTallEntityBlock implements SeatableBlock {
    public static BooleanProperty OPEN = BlockStateProperties.OPEN;

    public CardboardBoxTall() {
        super(BlockBehaviour.Properties.of(Material.WOOL).strength(1.0F).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never)
                .sound(SoundType.SCAFFOLDING));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    protected BlockEntity getBlockEntityForBlock(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER))
            pos = pos.above();
        return level.getBlockEntity(pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (getBlockEntityForBlock(level, pos, state) instanceof CardboardBoxTallBlockEntity blockEntity) {
            return blockEntity.hideEntity(player) ?
                    InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (getBlockEntityForBlock(level, pos, state) instanceof CardboardBoxTallBlockEntity blockEntity)
            if (blockEntity.getSeatedEntity() == player)
                return false;
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        if (getBlockEntityForBlock(level, pos, state) instanceof CardboardBoxTallBlockEntity blockEntity)
            if (blockEntity.getSeatedEntity() == entity)
                return false;
        return super.canEntityDestroy(state, level, pos, entity);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
            return Block.box(2.0, 0.0, 2.0, 14.0, 24.0, 14.0);
        else
            return Block.box(2.0, -16.0, 2.0, 14.0, 24.0 - 16.0, 14.0);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CardboardBoxTallBlockEntity(pos, state);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return blockState.getValue(HALF).equals(DoubleBlockHalf.UPPER);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return state.getValue(HALF).equals(DoubleBlockHalf.LOWER) ? null :
                createTickerHelper(type, ChangedBlockEntities.CARDBOARD_BOX_TALL.get(), CardboardBoxTallBlockEntity::tick);
    }

    private static final Vec3 SIT_OFFSET = Vec3.ZERO;
    @Override
    public Vec3 getSitOffset(BlockGetter level, BlockState state, BlockPos pos) {
        return SIT_OFFSET;
    }

    @Override
    public void onEnterSeat(BlockGetter level, BlockState state, BlockPos pos, @NotNull Entity entity) {
        SeatableBlock.super.onEnterSeat(level, state, pos, entity);

        if (getBlockEntityForBlock(level, pos, state) instanceof CardboardBoxTallBlockEntity blockEntity) {
            entity.level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ChangedSounds.BOW2, SoundSource.BLOCKS, 1.0f, 1.0f, true);
            blockEntity.ticksSinceChange = 0;
        }
    }

    @Override
    public void onExitSeat(BlockGetter level, BlockState state, BlockPos pos, @NotNull Entity entity) {
        SeatableBlock.super.onExitSeat(level, state, pos, entity);

        if (getBlockEntityForBlock(level, pos, state) instanceof CardboardBoxTallBlockEntity blockEntity) {
            entity.level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ChangedSounds.BOW2, SoundSource.BLOCKS, 1.0f, 1.0f, true);
            blockEntity.ticksSinceChange = 0;
        }
    }
}
