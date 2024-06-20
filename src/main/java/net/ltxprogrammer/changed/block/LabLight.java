package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LabLight extends AbstractCustomShapeBlock {
    /*
     * 0 1
     * */
    public static final IntegerProperty SECTION = IntegerProperty.create("section", 0, 1);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public static final VoxelShape SHAPE_WHOLE = Block.box(-16.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public LabLight(Properties properties) {
        super(properties.lightLevel((state) -> state.getValue(LabLight.POWERED) ? 15 : 0).emissiveRendering((state, level, pos) -> state.getValue(POWERED)));
        this.registerDefaultState(this.stateDefinition.any().setValue(SECTION, 0).setValue(POWERED, false));
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        if (p_60537_.getValue(SECTION) == 0)
            return super.getDrops(p_60537_, p_60538_);
        else
            return new ArrayList<>();
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54543_) {
        super.createBlockStateDefinition(p_54543_);
        p_54543_.add(SECTION).add(POWERED);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();

        boolean place = false;
        switch (direction) {
            case NORTH -> place = level.getBlockState(blockpos.east()).canBeReplaced(context);
            case EAST -> place = level.getBlockState(blockpos.south()).canBeReplaced(context);
            case SOUTH -> place = level.getBlockState(blockpos.west()).canBeReplaced(context);
            case WEST -> place = level.getBlockState(blockpos.north()).canBeReplaced(context);
        }

        if (!place) return null;
        switch (direction) {
            case NORTH -> direction = Direction.SOUTH;
            case EAST -> direction = Direction.WEST;
            case SOUTH -> direction = Direction.NORTH;
            case WEST -> direction = Direction.EAST;
        }

        var state = super.getStateForPlacement(context);
        if (state != null)
            return state.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())).setValue(FACING, direction);
        else
            return null;
    }

    protected BlockPos getBlockPos(BlockState p_52776_, BlockPos p_52778_, int section_num) {
        int x = 0;
        int self_num = p_52776_.getValue(SECTION);
        if (self_num == 0 && section_num == 1) x = -1;
        else if (self_num == 1 && section_num == 0) x = 1;

        switch (p_52776_.getValue(FACING)) {
            case NORTH -> {
                return p_52778_.offset(x, 0, 0);
            }
            case EAST -> {
                return p_52778_.offset(0, 0, x);
            }
            case SOUTH -> {
                return p_52778_.offset(-x, 0, 0);
            }
            case WEST -> {
                return p_52778_.offset(0, 0, -x);
            }
        }

        return null;
    }

    protected boolean hasNeighborSignal(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, int section_num) {
        return p_52777_.hasNeighborSignal(getBlockPos(p_52776_, p_52778_, section_num));
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block otherBlock, BlockPos otherPos, boolean p_52781_) {
        boolean flag = this.hasNeighborSignal(state, level, pos, 0) ||
                this.hasNeighborSignal(state, level, pos, 1);
        if (!this.defaultBlockState().is(otherBlock) && flag != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, flag), 2);
        }
    }

    protected BlockState getBlockState(BlockState p_52776_, LevelReader p_52777_, BlockPos p_52778_, int section_num) {
        return p_52777_.getBlockState(getBlockPos(p_52776_, p_52778_, section_num));
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack) {
        switch (state.getValue(FACING)) {
            case NORTH -> level.setBlock(pos.west(), state.setValue(SECTION, 1), 3);
            case SOUTH -> level.setBlock(pos.east(), state.setValue(SECTION, 1), 3);
            case EAST -> level.setBlock(pos.north(), state.setValue(SECTION, 1), 3);
            case WEST -> level.setBlock(pos.south(), state.setValue(SECTION, 1), 3);
        }
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        if (p_52783_.getValue(SECTION) == 0)
            return p_52784_.getBlockState(p_52785_.above()).isFaceSturdy(p_52784_, p_52785_.above(), Direction.DOWN);

        int section = p_52783_.getValue(SECTION);

        return (this.getBlockState(p_52783_, p_52784_, p_52785_, 0).is(this) || section == 0) &&
                (this.getBlockState(p_52783_, p_52784_, p_52785_, 1).is(this) || section == 1);
    }


    protected void preventCreativeDropFromBottomPart(Level p_52904_, BlockPos p_52905_, BlockState p_52906_, Player p_52907_) {
        int section = p_52906_.getValue(SECTION);
        if (section == 1) {
            BlockPos blockpos = getBlockPos(p_52906_, p_52905_, 0);
            BlockState blockstate = p_52904_.getBlockState(blockpos);
            if (blockstate.is(p_52906_.getBlock()) && blockstate.getValue(SECTION) == 0) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                p_52904_.setBlock(blockpos, blockstate1, 35);
                p_52904_.levelEvent(p_52907_, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public void playerWillDestroy(Level p_52878_, BlockPos p_52879_, BlockState p_52880_, Player p_52881_) {
        if (!p_52878_.isClientSide) {
            if (p_52881_.isCreative()) {
                preventCreativeDropFromBottomPart(p_52878_, p_52879_, p_52880_, p_52881_);
            } else if (p_52880_.getValue(SECTION) == 1) {
                dropResources(p_52880_, p_52878_, p_52879_, (BlockEntity)null, p_52881_, p_52881_.getMainHandItem());
            }
        }

        super.playerWillDestroy(p_52878_, p_52879_, p_52880_, p_52881_);
    }

    public void destroy(LevelAccessor p_49860_, BlockPos p_49861_, BlockState p_49862_) {
        if (p_49862_.getValue(SECTION) == 1)
            p_49860_.destroyBlock(getBlockPos(p_49862_, p_49861_, 0), true);
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        if (p_54559_.getValue(SECTION) == 0)
            return RenderShape.MODEL;
        else
            return RenderShape.INVISIBLE;
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        VoxelShape shape;

        double x = 0.0D;
        double z = 0.0D;

        shape = calculateShapes(p_60547_.getValue(FACING), SHAPE_WHOLE);
        switch(p_60547_.getValue(FACING)) {
            case NORTH -> x = 1.0D;
            case EAST -> z = 1.0D;
            case SOUTH -> x = -1.0D;
            case WEST -> z = -1.0D;
        }

        switch (p_60547_.getValue(SECTION)) {
            case 0 -> { return shape; }
            case 1 -> { return shape.move(x, 0.0D, z); }
        }

        return shape;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        int section = p_52796_.getValue(SECTION);
        Direction face = p_52796_.getValue(FACING);
        if (p_52797_.getAxis() == Direction.Axis.Y)
            return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
        else {
            if ((face == Direction.NORTH) && p_52797_.getAxis() == Direction.Axis.X) {
                if (section < 1 && p_52797_ == Direction.EAST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if ( p_52797_ == Direction.WEST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            else if ((face == Direction.SOUTH) && p_52797_.getAxis() == Direction.Axis.X) {
                if (section < 1 && p_52797_ == Direction.WEST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (p_52797_ == Direction.EAST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            else if ((face == Direction.EAST) && p_52797_.getAxis() == Direction.Axis.Z) {
                if (section < 1 && p_52797_ == Direction.SOUTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (p_52797_ == Direction.NORTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            else if ((face == Direction.WEST) && p_52797_.getAxis() == Direction.Axis.Z) {
                if (section < 1 && p_52797_ == Direction.NORTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (p_52797_ == Direction.SOUTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            return section == 0 && p_52797_ == Direction.DOWN && !p_52796_.canSurvive(p_52799_, p_52800_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
        }
    }
}
