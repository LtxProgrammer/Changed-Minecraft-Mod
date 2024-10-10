package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.robot.AbstractRobot;
import net.ltxprogrammer.changed.entity.robot.ChargerType;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

import java.util.Random;

public class RoombaCharger extends AbstractCustomShapeBlock implements IRobotCharger {
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 4, 14);

    public RoombaCharger(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OCCUPIED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OCCUPIED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.randomTick(state, level, pos, random);
        broadcastPosition(level, pos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState otherState, boolean p_60570_) {
        super.onPlace(state, level, pos, otherState, p_60570_);
        broadcastPosition(level, pos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(OCCUPIED)) {
            ItemStack item = player.getItemInHand(hand);
            if (item.is(ChangedItems.ROOMBA.get())) {
                level.setBlockAndUpdate(pos, state.setValue(OCCUPIED, true));
                level.scheduleTick(pos, this, 20 * 60, TickPriority.NORMAL);
                item.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            else
                return InteractionResult.PASS;
        } else {
            level.setBlockAndUpdate(pos, state.setValue(OCCUPIED, false));
            player.addItem(new ItemStack(ChangedItems.ROOMBA.get()));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        if (state.getValue(OCCUPIED)) {
            BlockPos spawnPos = pos.relative(state.getValue(FACING));
            var robot = ChangedEntities.ROOMBA.get().create(level);
            if (robot != null) {
                level.setBlockAndUpdate(pos, state.setValue(OCCUPIED, false));
                robot.moveTo(spawnPos, 0, 0);
                level.addFreshEntity(robot);

                broadcastPosition(level, pos);
            }
        }
    }

    @Override
    public ChargerType getChargerType() {
        return ChargerType.ROOMBA;
    }

    @Override
    public void acceptRobot(BlockState state, Level level, BlockPos pos, AbstractRobot robot) {
        if (state.getValue(OCCUPIED))
            return;

        if (robot instanceof Roomba) {
            level.setBlockAndUpdate(pos, state.setValue(OCCUPIED, true));
            level.scheduleTick(pos, this, 20 * 60, TickPriority.NORMAL);
            robot.discard();

            broadcastPosition(level, pos);
        }
    }
}
