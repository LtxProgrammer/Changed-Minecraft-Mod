package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.KeypadBlockEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class KeypadBlock extends AbstractCustomShapeEntityBlock {
    public static final BooleanProperty POWERED = LeverBlock.POWERED;
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 1.0D, 11.0D, 15.0D, 10.0D, 16.0D);

    public KeypadBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).sound(SoundType.METAL).strength(3.0F, 3.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.FALSE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KeypadBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MenuProvider provider) {
                if (state.getValue(POWERED)) {
                    ChangedSounds.broadcastSound(Objects.requireNonNull(level.getServer()), ChangedSounds.KEY, pos, 1.0f, 1.0f);
                    level.setBlockAndUpdate(pos, state.setValue(POWERED, Boolean.FALSE));
                    this.updateNeighbours(state, level, pos);
                } else
                    player.openMenu(provider);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, entity, itemStack);
        if (entity instanceof Player player)
            player.openMenu(getMenuProvider(blockState, level, blockPos));
    }

    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState blockState, boolean noUpdate) {
        if (!noUpdate && !state.is(blockState.getBlock())) {
            if (state.getValue(POWERED)) {
                this.updateNeighbours(state, level, blockPos);
            }

            super.onRemove(state, level, blockPos, blockState, noUpdate);
        }
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter level, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) ? 15 :
                super.getSignal(blockState, level, blockPos, direction);
    }

    public int getDirectSignal(BlockState blockState, BlockGetter level, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) && blockState.getValue(FACING) == direction ? 15 : 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return state.getValue(POWERED);
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public void updateNeighbours(BlockState state, Level level, BlockPos blockPos) {
        level.updateNeighborsAt(blockPos, this);
        level.updateNeighborsAt(blockPos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block source, BlockPos sourcePos, boolean simulate) {
        super.neighborChanged(blockState, level, blockPos, source, sourcePos, simulate);
        if (!blockState.canSurvive(level, blockPos)) {
            BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            dropResources(blockState, level, blockPos, blockentity);
            level.removeBlock(blockPos, false);

            for(Direction direction : Direction.values()) {
                level.updateNeighborsAt(blockPos.relative(direction), this);
            }

        }
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        return level.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite())).isFaceSturdy(level,
                blockPos.relative(blockState.getValue(FACING).getOpposite()),
                blockState.getValue(FACING));
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, Random random) {
        super.tick(blockState, level, blockPos, random);
        ChangedSounds.broadcastSound(level.getServer(), ChangedSounds.CHIME2, blockPos, 1, 1);
    }

    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return getInteractionShape(blockState, level, blockPos);
    }

    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return getInteractionShape(blockState, level, blockPos);
    }

    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return calculateShapes(blockState.getValue(FACING), SHAPE_WHOLE);
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return getInteractionShape(blockState, level, blockPos);
    }
}
