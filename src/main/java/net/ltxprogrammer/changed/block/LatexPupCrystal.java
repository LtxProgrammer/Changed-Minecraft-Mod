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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class LatexPupCrystal extends AbstractLatexCrystal {
    private final LatexVariant<?> variant;
    private final int multiply;

    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public LatexPupCrystal(LatexVariant<?> variant, int multiply, Supplier<? extends Item> crystal, Properties properties) {
        super(variant, crystal, properties);
        this.variant = variant;
        this.multiply = multiply;

        this.registerDefaultState(getStateDefinition().any().setValue(EXTENDED, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXTENDED, HALF);
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
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.8F, 0.75D, 0.8F));

        if (variant == null) return;

        if (entity instanceof LivingEntity le && !(entity instanceof LatexEntity)) {
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

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (direction.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (direction == Direction.UP) || otherState.is(this) && otherState.getValue(HALF) != doubleblockhalf) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, otherState, level, pos, otherPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }
}
