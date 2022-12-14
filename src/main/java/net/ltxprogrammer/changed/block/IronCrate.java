package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public class IronCrate extends AbstractCustomShapeTallBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 24.0D, 14.0D);
    public static final VoxelShape SHAPE_OCC = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);

    public IronCrate(Properties properties) {
        super(properties);
    }


    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return switch (p_60547_.getValue(HALF)) {
            case UPPER -> SHAPE_WHOLE.move(0.0, -1.0, 0.0);
            case LOWER -> SHAPE_WHOLE;
        };
    }


}
