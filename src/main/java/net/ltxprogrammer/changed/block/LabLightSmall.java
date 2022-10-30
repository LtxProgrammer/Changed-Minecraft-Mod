package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.ltxprogrammer.changed.block.LabLight.POWERED;

public class LabLightSmall extends AbstractCustomShapeBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public LabLightSmall(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54543_) {
        super.createBlockStateDefinition(p_54543_);
        p_54543_.add(POWERED);
    }

    public void neighborChanged(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, Block p_52779_, BlockPos p_52780_, boolean p_52781_) {
        boolean flag = p_52777_.hasNeighborSignal(p_52778_);
        if (!this.defaultBlockState().is(p_52779_) && flag != p_52776_.getValue(POWERED)) {
            p_52777_.setBlock(p_52778_, p_52776_.setValue(POWERED, Boolean.valueOf(flag)), 2);
        }
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
       return p_52784_.getBlockState(p_52785_.above()).isFaceSturdy(p_52784_, p_52785_.above(), Direction.DOWN);
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
}
