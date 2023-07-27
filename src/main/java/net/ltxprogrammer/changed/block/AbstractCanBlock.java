package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbstractCanBlock extends AbstractCustomShapeBlock {
    public static final float AABB_OFFSET = 4.0f;
    public static final VoxelShape SHAPE_WHOLE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 7.0D, 10.0D);

    public AbstractCanBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.COPPER).strength(0.5F).noOcclusion());
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

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        Vec3 vec3 = p_60547_.getOffset(p_60548_, p_60549_);
        return SHAPE_WHOLE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Vec3 vec3 = p_60555_.getOffset(p_60556_, p_60557_);
        return SHAPE_WHOLE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
}
