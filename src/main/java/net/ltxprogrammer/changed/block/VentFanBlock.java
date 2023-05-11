package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentFanBlock extends DirectionalBlock implements NonLatexCoverableBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public VentFanBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().sound(SoundType.COPPER).strength(3.0F, 5.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.FALSE));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState otherState, boolean flag) {
        if (!otherState.is(state.getBlock())) {
            boolean wantOn = level.getBestNeighborSignal(pos) > 0;
            if (wantOn != state.getValue(POWERED))
                level.setBlockAndUpdate(pos, state.setValue(POWERED, wantOn));
        }
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState state, Entity entity) {
        super.stepOn(level, blockPos, state, entity);
        if (state.getValue(FACING) == Direction.UP && state.getValue(POWERED))
            entity.hurt(ChangedDamageSources.FAN, 1);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance()));
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block source, BlockPos sourcePos, boolean flag) {
        boolean wantOn = level.getBestNeighborSignal(pos) > 0;
        if (wantOn != state.getValue(POWERED))
            level.setBlockAndUpdate(pos, state.setValue(POWERED, wantOn));
        super.neighborChanged(state, level, pos, source, sourcePos, flag);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
