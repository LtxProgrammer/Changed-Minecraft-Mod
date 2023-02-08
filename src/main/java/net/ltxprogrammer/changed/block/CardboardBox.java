package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CardboardBox extends AbstractCustomShapeTallEntityBlock {
    public static BooleanProperty OPEN = BlockStateProperties.OPEN;

    public CardboardBox() {
        super(BlockBehaviour.Properties.of(Material.WOOL).strength(2.0F, 12.0F).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never)
                .sound(SoundType.SCAFFOLDING));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CardboardBoxBlockEntity(pos, state);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER))
            pos = pos.above();
        if (level.getBlockEntity(pos) instanceof CardboardBoxBlockEntity blockEntity) {
            return blockEntity.hideEntity(player) ?
                    InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof CardboardBoxBlockEntity blockEntity) {
            blockEntity.forceOutEntity();
            blockEntity.entityHolder.remove(Entity.RemovalReason.DISCARDED);
        }
        var player = level.getNearestPlayer(TargetingConditions.forNonCombat(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        if (player != null && player.isInvisible())
            player.setInvisible(false);
        super.destroy(level, pos, state);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof CardboardBoxBlockEntity blockEntity)
            if (blockEntity.entity == null || !blockEntity.entity.is(entity))
                return super.canEntityDestroy(state, level, pos, entity);
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52901_) {
        super.createBlockStateDefinition(p_52901_);
        p_52901_.add(OPEN);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
            return Block.box(2.0, 0.0, 2.0, 14.0, 24.0, 14.0);
        else
            return Block.box(2.0, -16.0, 2.0, 14.0, 24.0 - 16.0, 14.0);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return state.getValue(HALF).equals(DoubleBlockHalf.LOWER) ? null :
                createTickerHelper(type, ChangedBlockEntities.CARDBOARD_BOX.get(), CardboardBoxBlockEntity::tick);
    }
}
