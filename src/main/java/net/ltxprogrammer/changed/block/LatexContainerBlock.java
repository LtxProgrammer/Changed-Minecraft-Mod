package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.LatexContainerBlockEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class LatexContainerBlock extends AbstractCustomShapeTallEntityBlock implements NonLatexCoverableBlock, Fallable {
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 24, 12.0D);

    public LatexContainerBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().sound(SoundType.GLASS).strength(3.0F, 5.0F).requiresCorrectToolForDrops());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private LatexContainerBlockEntity getBlockEntity(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LatexContainerBlockEntity blockEntity)
            return blockEntity;
        return null;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            pos = pos.below();
        LatexContainerBlockEntity blockEntity = getBlockEntity(state, level, pos);
        if (blockEntity == null)
            return InteractionResult.PASS;

        ItemStack itemStack = player.getItemInHand(hand);
        var nStack = blockEntity.tryUse(itemStack);
        if (nStack != null) {
            player.addItem(nStack);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    private int processBreak(Level level, BlockPos blockPos, LatexType type, int remaining, AtomicBoolean placedFluid) {
        if (remaining == 16) {
            var pupType = type.pup.get();
            if (pupType != null) {
                var pup = pupType.create(level);
                if (pup != null) {
                    pup.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    level.addFreshEntity(pup);
                    return 16;
                }
            }
        }

        switch (level.getRandom().nextInt(6)) {
            case 0:
                return 1; // Destroy goo
            case 1:
                if (remaining >= 4 && !placedFluid.get()) {
                    level.setBlockAndUpdate(blockPos, type.gooBucket.get().fluid.get().defaultFluidState().createLegacyBlock());
                    popResource(level, blockPos, new ItemStack(this));
                    placedFluid.set(true);
                    return 4; // Put goo fluid
                }

                else {
                    popResource(level, blockPos, new ItemStack(type.goo.get()));
                    return 1; // Drop goo item
                }
            case 2:
                int attempts = 3;
                while (attempts > 0) {
                    if (AbstractLatexBlock.tryCover(
                            level,
                            blockPos
                                    .relative(Direction.getRandom(level.getRandom()))
                                    .relative(Direction.getRandom(level.getRandom())),
                            type))
                        return 1;

                    attempts--;
                }
            default:
                popResource(level, blockPos, new ItemStack(type.goo.get()));
                return 1; // Drop goo item
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean noSimulate) {
        var blockEntity = getBlockEntity(state, level, blockPos);
        super.onRemove(state, level, blockPos, newState, noSimulate);

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && blockEntity != null && blockEntity.getFillType() != LatexType.NEUTRAL &&
                blockEntity.getFillLevel() > 0 && !noSimulate) {
            int fill = blockEntity.getFillLevel();
            AtomicBoolean atomic = new AtomicBoolean(false);
            while (fill > 0)
                fill -= processBreak(level, blockPos, blockEntity.getFillType(), fill, atomic);
        }
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return switch (p_60547_.getValue(HALF)) {
            case UPPER -> SHAPE_WHOLE.move(0.0, -1.0, 0.0);
            case LOWER -> SHAPE_WHOLE;
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LatexContainerBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    private void survivedFall(Level level, BlockPos pos, BlockState state) {
        //FluidState fluid = level.getFluidState(pos.above());

        level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
    }

    private double getFallDistance(FallingBlockEntity falling) {
        return (double)falling.getStartPos().getY() - falling.getY();
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState fellOn, FallingBlockEntity falling) {
        var blockEntity = level.getBlockEntity(pos, ChangedBlockEntities.LATEX_CONTAINER.get());

        blockEntity.ifPresent(container -> {
            if (falling.blockData != null)
                container.load(falling.blockData);
        });

        if (getFallDistance(falling) < 2) {
            survivedFall(level, pos, state);
            return;
        } // Container fell too short
        if (!level.getFluidState(pos).isEmpty()) {
            survivedFall(level, pos, state);
            return;
        } // Container fell in fluid

        blockEntity.ifPresent(container -> {
            if (container.getFillType() != LatexType.DARK_LATEX || container.getFillLevel() == 0)
                return;

            level.getEntitiesOfClass(LivingEntity.class, new AABB(pos)).forEach(livingEntity -> {
                ProcessTransfur.progressTransfur(livingEntity, 15.0f, LatexVariant.DARK_LATEX_WOLF_PARTIAL);
            });
        });

        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            var blockEntity = level.getBlockEntity(pos, ChangedBlockEntities.LATEX_CONTAINER.get());
            CompoundTag blockData = blockEntity.map(BlockEntity::saveWithFullMetadata).orElse(null);

            // Remove without breaking
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 64 | 3);
            level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 64 | 3);

            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level, pos, state);
            fallingBlockEntity.blockData = blockData;

            this.falling(fallingBlockEntity);
        }
    }

    protected void falling(FallingBlockEntity blockEntity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState state) {
        Material material = state.getMaterial();
        return state.isAir() || state.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_53236_, boolean p_53237_) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }
}
