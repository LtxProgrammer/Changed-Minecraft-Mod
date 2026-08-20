package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.ComputerBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class Computer extends AbstractCustomShapeEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE_SCREEN = Block.box(0.0D, 3.0D, 11.0D, 16.0D, 14.0D, 13.0D);
    public static final VoxelShape SHAPE_STAND = Block.box(6.0D, 1.0D, 13.0D, 10.0D, 8.0D, 14.0D);
    public static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 3.0D, 15.0D, 1.0D, 15.0D);
    public static final VoxelShape SHAPE_WHOLE = Shapes.or(SHAPE_SCREEN, SHAPE_STAND, SHAPE_BASE);

    public Computer(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder lootsParams) {
        var drops = super.getDrops(blockState, lootsParams);
        var blockEntity = (ComputerBlockEntity) lootsParams.getParameter(LootContextParams.BLOCK_ENTITY);

        drops.addAll(blockEntity.items);

        return drops;
    }

    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        super.setPlacedBy(p_52749_, p_52750_, p_52751_, p_52752_, p_52753_);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block source, BlockPos sourcePos, boolean simulate) {
        super.neighborChanged(blockState, level, blockPos, source, sourcePos, simulate);
        if (!blockState.canSurvive(level, blockPos)) {
            BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            dropResources(blockState, level, blockPos, blockentity);
            level.removeBlock(blockPos, false);
        }
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return calculateShapes(blockState.getValue(FACING), SHAPE_WHOLE);
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return getInteractionShape(blockState, level, blockPos);
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
    }

    public static boolean tryUseDisk(@Nullable Player player, Level level, BlockPos blockPos, BlockState state, ItemStack item) {
        return level.getBlockEntity(blockPos, ChangedBlockEntities.COMPUTER.get()).map(blockEntity -> {
            if (blockEntity.canPlaceItem(0, item)) {
                blockEntity.setItem(0, item.copyWithCount(1));
                item.shrink(1);
                level.playSound(null, blockPos, ChangedSounds.COMPUTER_DISC_INSERT.get(), SoundSource.BLOCKS, 1.0f, 0.9F + level.random.nextFloat() * 0.2F);
                return true;
            }

            return false;
        }).orElse(false);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer) {
            var blockEntity = level.getBlockEntity(pos, ChangedBlockEntities.COMPUTER.get()).orElse(null);
            if (blockEntity == null)
                return InteractionResult.FAIL;

            ItemStack heldItem = serverPlayer.getItemInHand(hand);
            if (heldItem.is(ChangedItems.COMPACT_DISC.get()) && tryUseDisk(player, level, pos, state, heldItem)) {
                return InteractionResult.CONSUME;
            }

            if (blockEntity.activeUser != null && blockEntity.activeUser != player)
                return InteractionResult.FAIL;
            blockEntity.activeUser = serverPlayer;

            NetworkHooks.openScreen(serverPlayer, getMenuProvider(state, level, pos), extra -> {
                extra.writeBlockPos(pos);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ComputerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ChangedBlockEntities.COMPUTER.get());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
        return typeE == typeA ? (BlockEntityTicker<A>)ticker : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends ComputerBlockEntity> newType) {
        return level.isClientSide ? null : createTickerHelper(type, newType, ComputerBlockEntity::serverTick);
    }
}
