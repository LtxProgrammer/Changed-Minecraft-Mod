package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LaserBeamBlock extends Block implements NonLatexCoverableBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0,LaserEmitterBlock.MAX_DISTANCE);
    private static final VoxelShape SHAPE_X = Shapes.box(0, 7, 7, 16, 9, 9);
    private static final VoxelShape SHAPE_Y = Shapes.box(7, 0, 7, 9, 16, 9);
    private static final VoxelShape SHAPE_Z = Shapes.box(7, 7, 0, 9, 9, 16);

    public LaserBeamBlock() {
        super(BlockBehaviour.Properties.of(Material.STRUCTURAL_AIR).strength(-1.0F, 3600000.0F).noDrops()
                .isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false)
                .lightLevel(blockState -> 4).emissiveRendering(ChangedBlocks::always));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, DISTANCE);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return getShape(blockState, level, blockPos, CollisionContext.empty());
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return switch (blockState.getValue(FACING).getAxis()) {
            case X -> SHAPE_X;
            case Y -> SHAPE_Y;
            case Z -> SHAPE_Z;
        };
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.entityInside(blockState, level, blockPos, entity);

        if (!(entity instanceof LivingEntity livingEntity))
            return;

        livingEntity.getArmorSlots().forEach(itemStack -> {
            if (itemStack.is(ChangedItems.BENIGN_PANTS.get()))
                ProcessTransfur.progressTransfur(livingEntity, 11000, LatexVariant.LATEX_BENIGN_WOLF.getFormId());
        });
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        Direction checkDir = blockState.getValue(FACING).getOpposite();
        int checkDistance = blockState.getValue(DISTANCE);
        BlockPos emitterPos = blockPos.relative(checkDir, checkDistance--);
        BlockState sourceState = level.getBlockState(emitterPos);
        if (!sourceState.is(ChangedBlocks.LASER_EMITTER.get()) || !sourceState.getValue(LaserEmitterBlock.POWERED))
            return false;
        while (checkDistance > 0) {
            BlockPos nextPos = blockPos.relative(checkDir, checkDistance--);
            BlockState nextState = level.getBlockState(nextPos);
            if (!nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT) && !nextState.is(ChangedBlocks.LASER_BEAM.get()))
                return false;
        }
        return super.canSurvive(blockState, level, blockPos);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return ChangedSounds.Types.NONE;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        Direction facing = state.getValue(FACING);
        if (facing.getOpposite() == direction) {
            if (otherState.isAir())
                return Blocks.AIR.defaultBlockState();
            if (otherState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                return super.updateShape(state, direction, otherState, level, pos, otherPos);

            int distance = state.getValue(DISTANCE);
            BlockPos emitterPos = pos.relative(facing.getOpposite(), distance);
            BlockPos nextPos = emitterPos.relative(facing, ++distance);
            if (!level.getBlockState(nextPos).canSurvive(level, nextPos))
                level.setBlock(nextPos, Blocks.AIR.defaultBlockState(), 3);

            return Blocks.AIR.defaultBlockState();
        }

        else if (facing == direction) {
            int distance = state.getValue(DISTANCE);
            BlockPos emitterPos = pos.relative(facing.getOpposite(), distance);
            while (distance < LaserEmitterBlock.MAX_DISTANCE) {
                BlockPos nextPos = emitterPos.relative(direction, ++distance);
                BlockState nextState = level.getBlockState(nextPos);
                if (!nextState.isAir() && !nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    break;
                if (!nextState.isAir() && nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    continue;
                level.setBlock(nextPos, state.setValue(LaserBeamBlock.DISTANCE, distance), 3);
            }

        }

        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
        return true;
    }
}
