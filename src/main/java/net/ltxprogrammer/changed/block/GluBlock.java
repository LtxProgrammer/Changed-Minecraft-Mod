package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.ltxprogrammer.changed.util.LocalUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GluBlock extends Block implements EntityBlock, GameMasterBlock {
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

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof GluBlockEntity && player.canUseGameMasterBlocks()) {
            UniversalDist.openGluBlock(player, (GluBlockEntity)blockentity);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GluBlockEntity(pos, state);
    }
}
