package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbstractCanBlock extends AbstractCustomShapeBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 7.0D, 10.0D);

    public AbstractCanBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.COPPER).strength(2.0F, 12.0F).noOcclusion());
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
}
