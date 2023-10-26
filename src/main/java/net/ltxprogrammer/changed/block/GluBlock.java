package net.ltxprogrammer.changed.block;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class GluBlock extends Block implements GameMasterBlock {
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    public GluBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction front = context.getClickedFace();
        Direction top;
        if (front.getAxis() == Direction.Axis.Y) {
            top = context.getHorizontalDirection().getOpposite();
        } else {
            top = Direction.UP;
        }

        return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION);
    }

    public static boolean canConnect(BlockState stateA, BlockState stateB) {
        if (!(stateA.getBlock() instanceof GluBlock) && !(stateA.getBlock() instanceof JigsawBlock))
            return false;
        if (!(stateB.getBlock() instanceof GluBlock) && !(stateB.getBlock() instanceof JigsawBlock))
            return false;

        FrontAndTop orientA = stateA.getValue(ORIENTATION);
        FrontAndTop orientB = stateB.getValue(ORIENTATION);
        if (orientA.front().getAxis() == Direction.Axis.Y && orientA.front() != orientB.front().getOpposite())
            return false;
        if (orientA.front().getAxis() == Direction.Axis.Y && orientA.top() != orientB.top())
            return false;
        if (orientA.front().getAxis() != Direction.Axis.Y && orientA.front() != orientB.front().getOpposite())
            return false;
        return true;
    }
}
