package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// Requires redstone power to be openable
public class LabDoor extends AbstractCustomShapeBlock {
    // Block.box(minX, minY, minZ, maxX, maxY, maxZ)
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    /*
    * 1 2
    * 0 3
    * */
    public static final IntegerProperty SECTION = IntegerProperty.create("section", 0, 3);

    public static final VoxelShape SHAPE_FRAME1 = Block.box(14.0D, 0.0D, 2.0D, 16.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME2 = Block.box(-16.0D, 0.0D, 2.0D, -14.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME3 = Block.box(-16.0D, 30.0D, 2.0D, 16.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME = Shapes.or(SHAPE_FRAME1, SHAPE_FRAME2, SHAPE_FRAME3);
    public static final VoxelShape SHAPE_DOOR = Block.box(-16.0D, 0.0D, 4.0D, 16.0D, 32.0D, 12.0D);
    public static final VoxelShape SHAPE_COLLISION_CLOSED = Shapes.or(SHAPE_FRAME, SHAPE_DOOR);

    public LabDoor(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false).setValue(POWERED, false).setValue(SECTION, 0));
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
        p_54543_.add(OPEN, POWERED, SECTION);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_52739_) {
        BlockPos blockpos = p_52739_.getClickedPos();
        Level level = p_52739_.getLevel();
        Direction direction = p_52739_.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(p_52739_)) {
            boolean place = false;
            switch (direction) {
                case NORTH -> place = level.getBlockState(blockpos.east()).canBeReplaced(p_52739_) && level.getBlockState(blockpos.east().above()).canBeReplaced(p_52739_);
                case EAST -> place = level.getBlockState(blockpos.south()).canBeReplaced(p_52739_) && level.getBlockState(blockpos.south().above()).canBeReplaced(p_52739_);
                case SOUTH -> place = level.getBlockState(blockpos.west()).canBeReplaced(p_52739_) && level.getBlockState(blockpos.west().above()).canBeReplaced(p_52739_);
                case WEST -> place = level.getBlockState(blockpos.north()).canBeReplaced(p_52739_) && level.getBlockState(blockpos.north().above()).canBeReplaced(p_52739_);
            }

            if (!place) return null;
            switch (direction) {
                case NORTH -> direction = Direction.SOUTH;
                case EAST -> direction = Direction.WEST;
                case SOUTH -> direction = Direction.NORTH;
                case WEST -> direction = Direction.EAST;
            }
            return this.defaultBlockState().setValue(FACING, direction).setValue(OPEN, Boolean.valueOf(false));
        } else {
            return null;
        }
    }

    protected BlockPos getBlockPos(BlockState p_52776_, BlockPos p_52778_, int section_num) {
        int x = 0, y = 0;
        int self_num = p_52776_.getValue(SECTION);
        if (self_num <= 1 && section_num > 1) x = 1;
        else if (self_num > 1 && section_num <= 1) x = -1;
        if ((self_num == 1 || self_num == 2) && (section_num == 0 || section_num == 3)) y = -1;
        else if ((self_num == 0 || self_num == 3) && (section_num == 1 || section_num == 2)) y = 1;

        switch (p_52776_.getValue(FACING)) {
            case NORTH -> {
                return p_52778_.offset(x, y, 0);
            }
            case EAST -> {
                return p_52778_.offset(0, y, x);
            }
            case SOUTH -> {
                return p_52778_.offset(-x, y, 0);
            }
            case WEST -> {
                return p_52778_.offset(0, y, -x);
            }
        }

        return null;
    }

    protected boolean hasNeighborSignal(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, int section_num) {
        return p_52777_.hasNeighborSignal(getBlockPos(p_52776_, p_52778_, section_num));
    }

    protected BlockState getBlockState(BlockState p_52776_, LevelReader p_52777_, BlockPos p_52778_, int section_num) {
        return p_52777_.getBlockState(getBlockPos(p_52776_, p_52778_, section_num));
    }

    public void neighborChanged(BlockState p_52776_, Level p_52777_, BlockPos p_52778_, Block p_52779_, BlockPos p_52780_, boolean p_52781_) {
        boolean flag = this.hasNeighborSignal(p_52776_, p_52777_, p_52778_, 0) ||
                this.hasNeighborSignal(p_52776_, p_52777_, p_52778_, 1) ||
                this.hasNeighborSignal(p_52776_, p_52777_, p_52778_, 2) ||
                this.hasNeighborSignal(p_52776_, p_52777_, p_52778_, 3);
        if (!this.defaultBlockState().is(p_52779_) && flag != p_52776_.getValue(POWERED)) {
            if (flag != p_52776_.getValue(OPEN) && flag == false) {
                this.playSound(p_52777_, p_52778_, flag);
                p_52777_.gameEvent(flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_52778_);
            }

            p_52777_.setBlock(p_52778_, p_52776_.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(false)), 2);
        }

    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        p_52749_.setBlock(p_52750_.above(), p_52751_.setValue(SECTION, 1), 3);

        switch (p_52751_.getValue(FACING)) {
            case NORTH -> {
                p_52749_.setBlock(p_52750_.above().west(), p_52751_.setValue(SECTION, 2), 3);
                p_52749_.setBlock(p_52750_.west(), p_52751_.setValue(SECTION, 3), 3);
            }
            case SOUTH -> {
                p_52749_.setBlock(p_52750_.above().east(), p_52751_.setValue(SECTION, 2), 3);
                p_52749_.setBlock(p_52750_.east(), p_52751_.setValue(SECTION, 3), 3);
            }
            case EAST -> {
                p_52749_.setBlock(p_52750_.above().north(), p_52751_.setValue(SECTION, 2), 3);
                p_52749_.setBlock(p_52750_.north(), p_52751_.setValue(SECTION, 3), 3);
            }
            case WEST -> {
                p_52749_.setBlock(p_52750_.above().south(), p_52751_.setValue(SECTION, 2), 3);
                p_52749_.setBlock(p_52750_.south(), p_52751_.setValue(SECTION, 3), 3);
            }
        }
    }

    public boolean canSurvive(BlockState p_52783_, @NotNull LevelReader p_52784_, @NotNull BlockPos p_52785_) {
        if (p_52783_.getValue(SECTION) == 0)
            return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);

        int section = p_52783_.getValue(SECTION);

        return (this.getBlockState(p_52783_, p_52784_, p_52785_, 0).is(this) || section == 0)
                && (this.getBlockState(p_52783_, p_52784_, p_52785_, 1).is(this) || section == 1)
                && (this.getBlockState(p_52783_, p_52784_, p_52785_, 2).is(this) || section == 2)
                && (this.getBlockState(p_52783_, p_52784_, p_52785_, 3).is(this) || section == 3);
    }

    public boolean isPathfindable(BlockState p_52764_, @NotNull BlockGetter p_52765_, @NotNull BlockPos p_52766_, @NotNull PathComputationType p_52767_) {
        return switch (p_52767_) {
            case LAND, AIR -> p_52764_.getValue(OPEN);
            case WATER -> false;
        };
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
        VoxelShape shape = SHAPE_FRAME;

        double x = 0.0D;
        double z = 0.0D;

        if (p_60547_.getValue(OPEN)) {
            shape = calculateShapes(p_60547_.getValue(FACING), SHAPE_FRAME);
            switch (p_60547_.getValue(FACING)) {
                case NORTH -> x = 1.0D;
                case EAST -> z = 1.0D;
                case SOUTH -> x = -1.0D;
                case WEST -> z = -1.0D;
            }
        }
        else {
            shape = calculateShapes(p_60547_.getValue(FACING), SHAPE_COLLISION_CLOSED);
            switch (p_60547_.getValue(FACING)) {
                case NORTH -> x = 1.0D;
                case EAST -> z = 1.0D;
                case SOUTH -> x = -1.0D;
                case WEST -> z = -1.0D;
            }
        }

        switch (p_60547_.getValue(SECTION)) {
            case 0 -> { return shape; }
            case 1 -> { return shape.move(0, -1.0D, 0); }
            case 2 -> { return shape.move(x, -1.0D, z); }
            case 3 -> { return shape.move(x, 0.0D, z); }
        }

        return shape;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        int section = p_52796_.getValue(SECTION);
        Direction face = p_52796_.getValue(FACING);
        if (p_52797_.getAxis() == Direction.Axis.Y && section == 0 == (p_52797_ == Direction.UP)) {
            return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(OPEN, p_52798_.getValue(OPEN)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
        } else {
            if ((face == Direction.NORTH) && p_52797_.getAxis() == Direction.Axis.X) {
                if (section <= 1 && p_52797_ == Direction.EAST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (section > 1 && p_52797_ == Direction.WEST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(OPEN, p_52798_.getValue(OPEN)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            else if ((face == Direction.SOUTH) && p_52797_.getAxis() == Direction.Axis.X) {
                if (section <= 1 && p_52797_ == Direction.WEST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (section > 1 && p_52797_ == Direction.EAST) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(OPEN, p_52798_.getValue(OPEN)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            else if ((face == Direction.EAST) && p_52797_.getAxis() == Direction.Axis.Z) {
                if (section <= 1 && p_52797_ == Direction.SOUTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (section > 1 && p_52797_ == Direction.NORTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(OPEN, p_52798_.getValue(OPEN)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            else if ((face == Direction.WEST) && p_52797_.getAxis() == Direction.Axis.Z) {
                if (section <= 1 && p_52797_ == Direction.NORTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
                else if (section > 1 && p_52797_ == Direction.SOUTH) return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);

                else
                    return p_52798_.is(this) && p_52798_.getValue(SECTION) != section ? p_52796_.setValue(FACING, p_52798_.getValue(FACING)).setValue(OPEN, p_52798_.getValue(OPEN)).setValue(POWERED, p_52798_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
            }

            return section == 0 && p_52797_ == Direction.DOWN && !p_52796_.canSurvive(p_52799_, p_52800_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
        }
    }

    public InteractionResult use(BlockState p_54524_, Level p_54525_, BlockPos p_54526_, Player p_54527_, InteractionHand p_54528_, BlockHitResult p_54529_) {
        if (p_54524_.getValue(POWERED))
            setOpen(p_54525_, p_54524_, p_54526_, !p_54524_.getValue(OPEN));
        return InteractionResult.SUCCESS;
    }

    private void playSound(Level p_52760_, BlockPos p_52761_, boolean open) {
        p_52760_.playSound(null, p_52761_, open ? ChangedSounds.OPEN3 : ChangedSounds.CLOSE3, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public boolean isOpen(BlockState p_52816_) {
        return p_52816_.getValue(OPEN);
    }

    public void setOpen(Level p_153167_, BlockState p_153168_, BlockPos p_153169_, boolean p_153170_) {
        if (p_153168_.is(this) && p_153168_.getValue(OPEN) != p_153170_) {
            p_153167_.setBlock(p_153169_, p_153168_.setValue(OPEN, Boolean.valueOf(p_153170_)), 10);
            this.playSound(p_153167_, p_153169_, p_153170_);
        }
    }
}
