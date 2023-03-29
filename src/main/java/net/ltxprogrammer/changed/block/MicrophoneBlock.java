package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MicrophoneBlock extends AbstractCustomShapeBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);

    public MicrophoneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENABLED);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        super.setPlacedBy(p_52749_, p_52750_, p_52751_, p_52752_, p_52753_);
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_,
                p_52785_.below(),
                Direction.UP);
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        level.setBlockAndUpdate(pos, state.setValue(ENABLED, !state.getValue(ENABLED)));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
