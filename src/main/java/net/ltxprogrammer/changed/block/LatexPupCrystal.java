package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class LatexPupCrystal extends AbstractLatexCrystal {
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 25.0D, 12.0D);
    public static final VoxelShape SHAPE_SMALL = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);

    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    private final LatexVariant<?> variant;
    private final int multiply;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public LatexPupCrystal(LatexVariant<?> variant, int multiply, Supplier<? extends Item> crystal, Properties properties) {
        super(variant, crystal, properties);
        this.variant = variant;
        this.multiply = multiply;

        this.registerDefaultState(getStateDefinition().any().setValue(EXTENDED, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        if (p_60547_.getValue(EXTENDED)) {

            if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
                return SHAPE_WHOLE;
            else
                return SHAPE_WHOLE.move(0, -1, 0);
        }
        else {
            if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
                return SHAPE_SMALL;
            else
                return SHAPE_SMALL.move(0, -1, 0);
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXTENDED, HALF);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            this.retract(level.getBlockState(pos.below()), level, pos.below());
        else
            this.retract(state, level, pos);
    }

    public void extend(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(EXTENDED)) { // Check if both blocks are used
            if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                var upState = level.getBlockState(pos.above());
                if (upState.isAir())
                    level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
            }
        }

        else {
            level.playSound(null, pos, ChangedSounds.ICE2, SoundSource.BLOCKS, 1.0f, 1.0f);

            level.setBlockAndUpdate(pos, state.setValue(EXTENDED, true));
            var upState = level.getBlockState(pos.above());
            if (upState.isAir())
                level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(EXTENDED, true));

            level.scheduleTick(pos, this, level.getRandom().nextInt(/* 30 seconds */ 30 * 20, /* 5 minutes */ 300 * 20));
        }
    }

    public void retract(BlockState state, Level level, BlockPos pos) {
        if (!state.getValue(EXTENDED)) { // Check if both blocks are used
            if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                var upState = level.getBlockState(pos.above());
                if (upState.is(this))
                    level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            }
        }

        else {
            level.setBlockAndUpdate(pos, state.setValue(EXTENDED, false));
            var upState = level.getBlockState(pos.above());
            if (upState.is(this))
                level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.8F, 0.75D, 0.8F));

        if (variant == null) return;

        if (entity instanceof LivingEntity le && !(entity instanceof LatexEntity) && !le.isDeadOrDying()) {
            if (entity instanceof Player player && (ProcessTransfur.isPlayerLatex(player) || player.isCreative()))
                return;
            this.extend(state, level, pos);
            if (!level.isClientSide) {
                if (ProcessTransfur.progressTransfur(le, 8.3f, variant)) {
                    for (int i = 1; i < multiply; ++i) {
                        variant.spawnAtEntity(le);
                    }
                }
            }

        }
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ?
                super.canSurvive(blockState, level, pos) :
                level.getBlockState(pos.below()).is(this);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide && blockState.getValue(EXTENDED)) {
            if (player.isCreative()) {
                AbstractDoubleLatexCrystal.preventCreativeDropFromBottomPart(level, blockPos, blockState, player);
            } else {
                dropResources(blockState, level, blockPos, null, player, player.getMainHandItem());

                if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
                    var otherPos = blockPos.below();
                    var otherState = level.getBlockState(otherPos);

                    Block.dropResources(level.getBlockState(otherPos), level, otherPos, null, player, player.getMainHandItem());
                    level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
                }
            }
        }

        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    public void playerDestroy(Level p_52865_, Player p_52866_, BlockPos p_52867_, BlockState p_52868_, @Nullable BlockEntity p_52869_, ItemStack p_52870_) {
        if (p_52868_.getValue(EXTENDED))
            super.playerDestroy(p_52865_, p_52866_, p_52867_, Blocks.AIR.defaultBlockState(), p_52869_, p_52870_);
        else
            super.playerDestroy(p_52865_, p_52866_, p_52867_, p_52868_, p_52869_, p_52870_);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (direction.getAxis() != Direction.Axis.Y || (doubleblockhalf == DoubleBlockHalf.LOWER) != (direction == Direction.UP) || otherState.is(this) && otherState.getValue(HALF) != doubleblockhalf || otherState.isAir() && direction == Direction.UP) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, otherState, level, pos, otherPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public boolean shouldDrop(BlockState blockState) {
        return !blockState.getValue(EXTENDED) || super.shouldDrop(blockState);
    }
}
