package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.block.entity.LabDoorOpenerEntity;
import net.ltxprogrammer.changed.block.entity.OpenableDoor;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.world.inventory.InfuserMenu;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StasisChamber extends HorizontalDirectionalBlock implements PartialEntityBlock, OpenableDoor, SeatableBlock {
    public static final EnumProperty<ThreeXThreeSection> SECTION = EnumProperty.create("section", ThreeXThreeSection.class);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final Component CONTAINER_TITLE = Component.translatable("container.changed.stasis_chamber");

    public static final VoxelShape SHAPE_FRAME_LEFT = Block.box(25.0D, 2.0D, -11.0D, 27.0D, 46.0D, 27.0D);
    public static final VoxelShape SHAPE_FRAME_LEFT_OPEN = Block.box(25.0D, 2.0D, 0.0D, 27.0D, 46.0D, 27.0D);
    public static final VoxelShape SHAPE_FRAME_RIGHT = Block.box(-11.0D, 2.0D, -11.0D, -9.0D, 46.0D, 27.0D);
    public static final VoxelShape SHAPE_FRAME_RIGHT_OPEN = Block.box(-11.0D, 2.0D, 0.0D, -9.0D, 46.0D, 27.0D);

    public static final VoxelShape SHAPE_FRAME_BACK = Block.box(-11.0D, 2.0D, 25.0D, 27.0D, 46.0D, 27.0D);
    public static final VoxelShape SHAPE_FRAME_FRONT = Block.box(-11.0D, 2.0D, -11.0D, 27.0D, 46.0D, -9.0D);
    public static final VoxelShape SHAPE_FRAME_BOTTOM = Block.box(-11.0D, 0.0D, -11.0D, 27.0D, 2.0D, 27.0D);
    public static final VoxelShape SHAPE_FRAME_TOP = Block.box(-11.0D, 46.0D, -11.0D, 27.0D, 48.0D, 27.0D);

    public static final VoxelShape INTERACTION_SHAPE = Block.box(-11.0D, 0.0D, -11.0D, 27.0D, 48.0D, 27.0D);

    public static final VoxelShape SHAPE_COLLISION_CLOSED = Shapes.or(
            SHAPE_FRAME_LEFT, SHAPE_FRAME_RIGHT,
            SHAPE_FRAME_BACK, SHAPE_FRAME_FRONT,
            SHAPE_FRAME_BOTTOM, SHAPE_FRAME_TOP
    );
    public static final VoxelShape SHAPE_COLLISION_OPEN = Shapes.or(
            SHAPE_FRAME_LEFT_OPEN, SHAPE_FRAME_RIGHT_OPEN,
            SHAPE_FRAME_BACK,
            SHAPE_FRAME_BOTTOM, SHAPE_FRAME_TOP
    );

    private final RegistryObject<SoundEvent> open, close;

    protected static int getLightLevel(BlockState state) {
        return (state.getValue(ACTIVE) && state.getValue(SECTION) == ThreeXThreeSection.MIDDLE_TOP_MIDDLE) ? 15 : 0;
    }

    public StasisChamber(RegistryObject<SoundEvent> open, RegistryObject<SoundEvent> close) {
        super(Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 9.0F)
                .lightLevel(StasisChamber::getLightLevel));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SECTION, ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE)
                .setValue(OPEN, Boolean.FALSE)
                .setValue(ACTIVE, Boolean.FALSE));
        this.open = open;
        this.close = close;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = SHAPE_COLLISION_OPEN;

        if (state.getValue(OPEN)) {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_COLLISION_OPEN);
        }
        else {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_COLLISION_CLOSED);
        }

        var offset = state.getValue(SECTION).getOffset(state.getValue(FACING), ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE);
        return shape.move(offset.getX(), offset.getY(), offset.getZ());
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state.setValue(OPEN, true), level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        var offset = state.getValue(SECTION).getOffset(state.getValue(FACING), ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE);
        return INTERACTION_SHAPE.move(offset.getX(), offset.getY(), offset.getZ());
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var offset = state.getValue(SECTION).getOffset(state.getValue(FACING), ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE);
        return INTERACTION_SHAPE.move(offset.getX(), offset.getY(), offset.getZ());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, SECTION, OPEN, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxBuildHeight() - 2) {
            for (var sect : ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE.getOtherValues()) {
                if (!level.getBlockState(ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE.getRelative(blockpos, direction.getOpposite(), sect)).canBeReplaced(context))
                    return null;
            }

            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(OPEN, Boolean.FALSE).setValue(SECTION, ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE);
        } else {
            return null;
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return state.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos blockpos, BlockState blockState) {
        if (blockState.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE)
            super.spawnDestroyParticles(level, player, blockpos, blockState);
    }

    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState next, boolean noUpdate) {
        if (!blockState.is(next.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, blockPos, (Container)blockentity);
                level.updateNeighbourForOutputSignal(blockPos, this);
            }

            super.onRemove(blockState, level, blockPos, next, noUpdate);
        }
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    public BlockPos getBlockEntityPos(BlockState state, BlockPos pos) {
        return state.getValue(SECTION).getRelative(pos, state.getValue(FACING), ThreeXThreeSection.CENTER);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos bePos = getBlockEntityPos(state, pos);
        BlockState beState = level.getBlockState(bePos);
        StasisChamberBlockEntity blockEntity = level.getBlockEntity(pos, ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);

        if (blockEntity != null && blockEntity.getChamberedEntity().map(chambered -> chambered == player).orElse(false))
            return InteractionResult.FAIL;

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, getMenuProvider(beState, level, bePos), extra -> {
                extra.writeBlockPos(bePos);
                extra.writeBoolean(false);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlock(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect), 18);
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, ThreeXThreeSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction> allCheckOrDir) {
        if (allCheckOrDir.left().isPresent() && !allCheckOrDir.left().get() && state.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrDir.left().isPresent() && allCheckOrDir.left().get() ? Arrays.stream(ThreeXThreeSection.values()).toList() : thisSect.getOtherValues()) {
            if (allCheckOrDir.right().isPresent()) {
                if (!thisSect.isRelative(sect, state.getValue(FACING), allCheckOrDir.right().get()))
                    continue;
            }

            var other = level.getBlockState(thisSect.getRelative(pos, state.getValue(FACING), sect));
            if (other.is(this) && other.getValue(SECTION) == sect)
                continue;
            return false;
        }

        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canSurvive(state, level, pos, Either.left(false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherBlockPos) {
        if (!this.canSurvive(state, level, pos, Either.right(direction)))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, otherState, level, pos, otherBlockPos);
    }

    protected void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        var section = state.getValue(SECTION);
        if (section != ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventCreativeDropFromBottomPart(level, pos, state, player);
            } else if (state.getValue(SECTION) != ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }

    /*@Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return super.mirror(state, mirror);
        else {
            return super.mirror(state, mirror).setValue(SECTION, state.getValue(SECTION).getHorizontalNeighbor());
        }
    }*/

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StasisChamberBlockEntity(pos, state);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return blockState.getValue(SECTION) == ThreeXThreeSection.CENTER;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ChangedBlockEntities.STASIS_CHAMBER.get());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
        return typeE == typeA ? (BlockEntityTicker<A>)ticker : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends StasisChamberBlockEntity> newType) {
        return level.isClientSide ? null : createTickerHelper(type, newType, StasisChamberBlockEntity::serverTick);
    }

    @Override
    public boolean openDoor(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(OPEN))
            return false;

        var wantState = true;
        var thisSect = state.getValue(SECTION);
        for (var sect : ThreeXThreeSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(OPEN, wantState));
            level.gameEvent(GameEvent.BLOCK_OPEN, pos, GameEvent.Context.of(state));
        }
        level.playSound(null, pos, open.get(), SoundSource.BLOCKS, 1, 1);
        return true;
    }

    @Override
    public boolean closeDoor(BlockState state, Level level, BlockPos pos) {
        if (!state.getValue(OPEN))
            return false;

        var wantState = false;
        var thisSect = state.getValue(SECTION);
        for (var sect : ThreeXThreeSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(OPEN, wantState));
            level.gameEvent(GameEvent.BLOCK_CLOSE, pos, GameEvent.Context.of(state));
        }
        level.playSound(null, pos, close.get(), SoundSource.BLOCKS, 1, 1);
        return true;
    }

    @Override
    public boolean isOpen(BlockState state, Level level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    public boolean markAsActive(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(ACTIVE))
            return false;

        var wantState = true;
        var thisSect = state.getValue(SECTION);
        for (var sect : ThreeXThreeSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(ACTIVE, wantState));
        }

        return true;
    }

    public boolean markAsInactive(BlockState state, Level level, BlockPos pos) {
        if (!state.getValue(ACTIVE))
            return false;

        var wantState = false;
        var thisSect = state.getValue(SECTION);
        for (var sect : ThreeXThreeSection.values()) {
            var nPos = thisSect.getRelative(pos, state.getValue(FACING), sect);
            var nBlock = level.getBlockState(nPos);
            if (nBlock.getBlock() != this)
                continue;
            level.setBlockAndUpdate(nPos, nBlock.setValue(ACTIVE, wantState));
        }

        return true;
    }

    public boolean isActive(BlockState state, Level level, BlockPos pos) {
        return state.getValue(ACTIVE);
    }

    @Override
    public AABB getDetectionSize(BlockState state, Level level, BlockPos pos) {
        return new AABB(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), ThreeXThreeSection.CENTER))
                .inflate(9.0 / 16.0,
                        14.0 / 16.0,
                        9.0 / 16.0);
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        if (this.stateHasBlockEntity(state))
            return level.getBlockEntity(pos, ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);
        return null;
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACING).getOpposite();
    }

    @Override
    public void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        return;
    }

    private static final Vec3 SIT_OFFSET = new Vec3(0.0D, -0.9D, 0.0D);
    private static final Vec3 SLEEP_OFFSET = new Vec3(0.4D, -0.3D, 0.0D);

    @Override
    public Vec3 getSitOffset(BlockGetter level, BlockState state, BlockPos pos) {
        return SIT_OFFSET;
    }

    @Override
    public Vec3 getSleepOffset(BlockGetter level, BlockState state, BlockPos pos) {
        return SLEEP_OFFSET;
    }

    public static boolean isEntityStabilized(LivingEntity livingEntity) {
        if (livingEntity.getVehicle() instanceof SeatEntity seatEntity) {
            return livingEntity.level().getBlockEntity(seatEntity.getAttachedBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get())
                    .map(StasisChamberBlockEntity::isStabilized)
                    .orElse(false);
        }

        return false;
    }

    public static boolean isEntityCaptured(LivingEntity livingEntity) {
        if (livingEntity.getVehicle() instanceof SeatEntity seatEntity) {
            return livingEntity.level().getBlockEntity(seatEntity.getAttachedBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get())
                    .isPresent();
        }

        return false;
    }
}
