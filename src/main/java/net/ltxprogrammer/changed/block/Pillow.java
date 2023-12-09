package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.block.entity.PillowBlockEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Pillow extends BaseEntityBlock implements SeatableBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape AABB = Block.box(1.5D, 0.0D, 1.5D, 14.5D, 5.0D, 14.5D);
    private final DyeColor color;

    public Pillow(DyeColor color) {
        super(BlockBehaviour.Properties.of(Material.WOOL, color).sound(SoundType.WOOL));
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0).setValue(WATERLOGGED, false).setValue(OCCUPIED, false));
    }

    public DyeColor getColor() {
        return color;
    }

    public static BlockState getStateForRot16(BlockState existing, int rot16) {
        return switch (rot16) {
            case 0 -> existing.setValue(ROTATION, 0).setValue(FACING, Direction.NORTH);
            case 1 -> existing.setValue(ROTATION, 1).setValue(FACING, Direction.NORTH);
            case 2 -> existing.setValue(ROTATION, 2).setValue(FACING, Direction.NORTH);
            case 3 -> existing.setValue(ROTATION, 3).setValue(FACING, Direction.NORTH);
            case 4 -> existing.setValue(ROTATION, 0).setValue(FACING, Direction.EAST);
            case 5 -> existing.setValue(ROTATION, 1).setValue(FACING, Direction.EAST);
            case 6 -> existing.setValue(ROTATION, 2).setValue(FACING, Direction.EAST);
            case 7 -> existing.setValue(ROTATION, 3).setValue(FACING, Direction.EAST);
            case 8 -> existing.setValue(ROTATION, 0).setValue(FACING, Direction.SOUTH);
            case 9 -> existing.setValue(ROTATION, 1).setValue(FACING, Direction.SOUTH);
            case 10 -> existing.setValue(ROTATION, 2).setValue(FACING, Direction.SOUTH);
            case 11 -> existing.setValue(ROTATION, 3).setValue(FACING, Direction.SOUTH);
            case 12 -> existing.setValue(ROTATION, 0).setValue(FACING, Direction.WEST);
            case 13 -> existing.setValue(ROTATION, 1).setValue(FACING, Direction.WEST);
            case 14 -> existing.setValue(ROTATION, 2).setValue(FACING, Direction.WEST);
            case 15 -> existing.setValue(ROTATION, 3).setValue(FACING, Direction.WEST);
            default -> throw new IllegalStateException("Unexpected value: " + rot16);
        };
    }

    public static int getRot16ForState(BlockState state) {
        return switch (state.getValue(FACING)) {
            case NORTH -> state.getValue(ROTATION);
            case EAST -> state.getValue(ROTATION) + 4;
            case SOUTH -> state.getValue(ROTATION) + 8;
            case WEST -> state.getValue(ROTATION) + 12;
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(FACING));
        };
    }

    public static Pillow forColor(DyeColor color) {
        return new Pillow(color);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return AABB.move(vec3.x, vec3.y, vec3.z);
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForRot16(this.defaultBlockState(), Mth.floor((double) (context.getRotation() * 16.0F / 360.0F) + 0.5D) & 15);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return getStateForRot16(state, mirror.mirror(getRot16ForState(state), 16));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED, OCCUPIED, FACING);
    }

    private static final Vec3 SIT_OFFSET = new Vec3(0.0D, 8.5D / 16.0D - 1.0D, 0.0D);

    @Override
    public Vec3 getSitOffset(BlockGetter level, BlockState state, BlockPos pos) {
        return SIT_OFFSET;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PillowBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ChangedBlockEntities.PILLOW.get(), PillowBlockEntity::tick);
    }

    private boolean kickLatexOutOfBed(Level level, BlockPos pos) {
        List<LatexEntity> list = level.getEntitiesOfClass(LatexEntity.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.get(0).stopSleeping();
            return true;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        boolean capableOfSleeping = ProcessTransfur.ifPlayerLatex(player, variant -> variant.getParent().is(ChangedTags.LatexVariants.CAN_SLEEP_ON_PILLOWS), () -> false);
        boolean canSleepNow = net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, Optional.of(pos));

        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (!BedBlock.canSetSpawn(level)) {
                level.removeBlock(pos, false);
                level.explode(null, DamageSource.badRespawnPointExplosion(), null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(OCCUPIED)) {
                if (!this.kickLatexOutOfBed(level, pos)) {
                    player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            } else if (capableOfSleeping) {
                player.startSleepInBed(pos).ifLeft((problem) -> {
                    if (problem != null) {
                        player.displayClientMessage(problem.getMessage(), true);
                    }

                });
                return InteractionResult.SUCCESS;
            } else if (level.getBlockEntity(pos) instanceof PillowBlockEntity blockEntity) {
                return blockEntity.sitEntity(player) ?
                        InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        if (player instanceof LivingEntity livingEntity) {
            var variant = LatexVariant.getEntityVariant(livingEntity);
            if (variant == null)
                return super.isBed(state, level, pos, player);
            return LatexVariant.getEntityVariant(livingEntity).is(ChangedTags.LatexVariants.CAN_SLEEP_ON_PILLOWS) || super.isBed(state, level, pos, player);
        }

        else {
            return super.isBed(state, level, pos, player);
        }
    }
}
