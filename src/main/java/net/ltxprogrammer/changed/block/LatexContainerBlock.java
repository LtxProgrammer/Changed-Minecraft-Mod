package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.LatexContainerBlockEntity;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class LatexContainerBlock extends AbstractCustomShapeTallEntityBlock implements CustomFallable, SimpleWaterloggedBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 24, 12.0D);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public LatexContainerBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().sound(SoundType.GLASS).strength(3.0F, 5.0F));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && state.is(this))
            return state.setValue(WATERLOGGED, AbstractCustomShapeTallBlock.shouldWaterlog(context.getLevel(), context.getClickedPos()));
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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
            var pupType = type.getPupEntityType(level.random);
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
                    level.setBlockAndUpdate(blockPos, Objects.requireNonNull(type.getBucketItem()).fluid.get().defaultFluidState().createLegacyBlock());
                    popResource(level, blockPos, new ItemStack(this));
                    placedFluid.set(true);
                    return 4; // Put goo fluid
                }

                else {
                    popResource(level, blockPos, new ItemStack(Objects.requireNonNull(type.getGooItem())));
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
                popResource(level, blockPos, new ItemStack(Objects.requireNonNull(type.getGooItem())));
                return 1; // Drop goo item
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean noSimulate) {
        if (state.getBlock() == newState.getBlock()) return;
        if (newState.isAir()) return;

        var blockEntity = getBlockEntity(state, level, blockPos);
        super.onRemove(state, level, blockPos, newState, noSimulate);

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && blockEntity != null && !blockEntity.getFillType().isAir() &&
                blockEntity.getFillLevel() > 0 && !noSimulate) {
            int fill = blockEntity.getFillLevel();
            AtomicBoolean atomic = new AtomicBoolean(false);
            while (fill > 0)
                fill -= processBreak(level, blockPos, blockEntity.getFillType(), fill, atomic);
        }
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return level.getBlockState(pos.below())
                    .isFaceSturdy(level, pos.below(), Direction.UP);
        } else {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
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
        level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, AbstractCustomShapeTallBlock.shouldWaterlog(level, pos.above())));
    }

    private double getFallDistance(FallingBlockEntity falling) {
        return (double)falling.getStartPos().getY() - falling.getY();
    }

    private static final Cacheable<ResourceLocation> MODEL_NAME = Cacheable.of(() -> DistExecutor.unsafeCallWhenOn(Dist.CLIENT,
            () -> () ->  new ModelResourceLocation(Changed.modResource("latex_container"), "inventory")));

    @Override
    public ResourceLocation getModelName() {
        return MODEL_NAME.get();
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

        level.playSound(null, pos, ChangedSounds.CONTAINER_BREAK.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

        blockEntity.ifPresent(container -> {
            if (container.getFillLevel() == 0)
                return;
            final var variant = container.getFillType().getTransfurVariant(TransfurCause.LATEX_CONTAINER_FELL, level.random);

            if (variant == null)
                return;

            level.getEntitiesOfClass(LivingEntity.class, new AABB(pos)).forEach(livingEntity -> {
                ProcessTransfur.progressTransfur(livingEntity, 15.0f, variant, TransfurContext.hazard(TransfurCause.LATEX_CONTAINER_FELL));
            });
        });

        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            if (!canSurvive(state, level, pos)) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
            return;
        }

        if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
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
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> blockEntity), CustomFallable.updateBlockData(blockEntity));
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_53236_, boolean p_53237_) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}