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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.ltxprogrammer.changed.init.ChangedSounds.OPEN2;

public class StasisChamber extends HorizontalDirectionalBlock implements NonLatexCoverableBlock, PartialEntityBlock, OpenableDoor, SeatableBlock {
    public static final EnumProperty<ThreeXThreeSection> SECTION = EnumProperty.create("section", ThreeXThreeSection.class);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.stasis_chamber");

    public static final VoxelShape SHAPE_FRAME_LEFT = Block.box(25.3D, 0.0D, 0.0D, 27.3D, 48.0D, 16.0D);
    public static final VoxelShape SHAPE_FRAME_RIGHT = Block.box(-11.3D, 0.0D, 0.0D, -9.3D, 48.0D, 16.0D);
    public static final VoxelShape SHAPE_FRAME_BACK = Shapes.or(
            Block.box(0.0D, 0.0D, 25.3D, 16.0D, 48.0D, 27.3D),

            Block.box(-1.0D, 2.0D, 24.0D, 0.0D, 46.0D, 26.3D),
            Block.box(-2.0D, 2.0D, 23.0D, -1.0D, 46.0D, 25.3D),
            Block.box(-3.0D, 2.0D, 22.0D, -2.0D, 46.0D, 24.3D),
            Block.box(-4.0D, 2.0D, 21.0D, -3.0D, 46.0D, 23.3D),
            Block.box(-5.0D, 2.0D, 20.0D, -4.0D, 46.0D, 22.3D),
            Block.box(-6.0D, 2.0D, 19.0D, -5.0D, 46.0D, 21.3D),
            Block.box(-7.0D, 2.0D, 18.0D, -6.0D, 46.0D, 20.3D),
            Block.box(-8.0D, 2.0D, 17.0D, -7.0D, 46.0D, 19.3D),
            Block.box(-9.0D, 2.0D, 16.0D, -8.0D, 46.0D, 18.3D),
            Block.box(-10.0D, 2.0D, 16.0D, -9.0D, 46.0D, 17.3D),

            Block.box(16.0D, 2.0D, 24.0D, 17.0D, 46.0D, 26.3D),
            Block.box(17.0D, 2.0D, 23.0D, 18.0D, 46.0D, 25.3D),
            Block.box(18.0D, 2.0D, 22.0D, 19.0D, 46.0D, 24.3D),
            Block.box(19.0D, 2.0D, 21.0D, 20.0D, 46.0D, 23.3D),
            Block.box(20.0D, 2.0D, 20.0D, 21.0D, 46.0D, 22.3D),
            Block.box(21.0D, 2.0D, 19.0D, 22.0D, 46.0D, 21.3D),
            Block.box(22.0D, 2.0D, 18.0D, 23.0D, 46.0D, 20.3D),
            Block.box(23.0D, 2.0D, 17.0D, 24.0D, 46.0D, 19.3D),
            Block.box(24.0D, 2.0D, 16.0D, 25.0D, 46.0D, 18.3D),
            Block.box(25.0D, 2.0D, 16.0D, 26.0D, 46.0D, 17.3D)
    );
    public static final VoxelShape SHAPE_FRAME_BOTTOM = Shapes.or(
            Block.box(-11.3D, 0.0D, 0.0D, 27.3D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, -11.3D, 16.0D, 2.0D, 27.3D),

            Block.box(-1.0D, 0.0D, 16.0D, 0.0D, 2.0D, 26.3D),
            Block.box(-2.0D, 0.0D, 16.0D, -1.0D, 2.0D, 25.3D),
            Block.box(-3.0D, 0.0D, 16.0D, -2.0D, 2.0D, 24.3D),
            Block.box(-4.0D, 0.0D, 16.0D, -3.0D, 2.0D, 23.3D),
            Block.box(-5.0D, 0.0D, 16.0D, -4.0D, 2.0D, 22.3D),
            Block.box(-6.0D, 0.0D, 16.0D, -5.0D, 2.0D, 21.3D),
            Block.box(-7.0D, 0.0D, 16.0D, -6.0D, 2.0D, 20.3D),
            Block.box(-8.0D, 0.0D, 16.0D, -7.0D, 2.0D, 19.3D),
            Block.box(-9.0D, 0.0D, 16.0D, -8.0D, 2.0D, 18.3D),
            Block.box(-10.0D, 0.0D, 16.0D, -9.0D, 2.0D, 17.3D),

            Block.box(16.0D, 0.0D, 16.0D, 17.0D, 2.0D, 26.3D),
            Block.box(17.0D, 0.0D, 16.0D, 18.0D, 2.0D, 25.3D),
            Block.box(18.0D, 0.0D, 16.0D, 19.0D, 2.0D, 24.3D),
            Block.box(19.0D, 0.0D, 16.0D, 20.0D, 2.0D, 23.3D),
            Block.box(20.0D, 0.0D, 16.0D, 21.0D, 2.0D, 22.3D),
            Block.box(21.0D, 0.0D, 16.0D, 22.0D, 2.0D, 21.3D),
            Block.box(22.0D, 0.0D, 16.0D, 23.0D, 2.0D, 20.3D),
            Block.box(23.0D, 0.0D, 16.0D, 24.0D, 2.0D, 19.3D),
            Block.box(24.0D, 0.0D, 16.0D, 25.0D, 2.0D, 18.3D),
            Block.box(25.0D, 0.0D, 16.0D, 26.0D, 2.0D, 17.3D),

            Block.box(-1.0D, 0.0D, -10.3D, 0.0D, 2.0D, 0.0D),
            Block.box(-2.0D, 0.0D, -9.3D, -1.0D, 2.0D, 0.0D),
            Block.box(-3.0D, 0.0D, -8.3D, -2.0D, 2.0D, 0.0D),
            Block.box(-4.0D, 0.0D, -7.3D, -3.0D, 2.0D, 0.0D),
            Block.box(-5.0D, 0.0D, -6.3D, -4.0D, 2.0D, 0.0D),
            Block.box(-6.0D, 0.0D, -5.3D, -5.0D, 2.0D, 0.0D),
            Block.box(-7.0D, 0.0D, -4.3D, -6.0D, 2.0D, 0.0D),
            Block.box(-8.0D, 0.0D, -3.3D, -7.0D, 2.0D, 0.0D),
            Block.box(-9.0D, 0.0D, -2.3D, -8.0D, 2.0D, 0.0D),
            Block.box(-10.0D, 0.0D, -1.3D, -9.0D, 2.0D, 0.0D),

            Block.box(16.0D, 0.0D, -10.3D, 17.0D, 2.0D, 0.0D),
            Block.box(17.0D, 0.0D, -9.3D, 18.0D, 2.0D, 0.0D),
            Block.box(18.0D, 0.0D, -8.3D, 19.0D, 2.0D, 0.0D),
            Block.box(19.0D, 0.0D, -7.3D, 20.0D, 2.0D, 0.0D),
            Block.box(20.0D, 0.0D, -6.3D, 21.0D, 2.0D, 0.0D),
            Block.box(21.0D, 0.0D, -5.3D, 22.0D, 2.0D, 0.0D),
            Block.box(22.0D, 0.0D, -4.3D, 23.0D, 2.0D, 0.0D),
            Block.box(23.0D, 0.0D, -3.3D, 24.0D, 2.0D, 0.0D),
            Block.box(24.0D, 0.0D, -2.3D, 25.0D, 2.0D, 0.0D),
            Block.box(25.0D, 0.0D, -1.3D, 26.0D, 2.0D, 0.0D)
    );
    public static final VoxelShape SHAPE_FRAME_TOP = Shapes.or(
            Block.box(-11.3D, 46.0D, 0.0D, 27.3D, 48.0D, 16.0D),
            Block.box(0.0D, 46.0D, -11.3D, 16.0D, 48.0D, 27.3D),

            Block.box(-1.0D, 46.0D, 16.0D, 0.0D, 48.0D, 26.3D),
            Block.box(-2.0D, 46.0D, 16.0D, -1.0D, 48.0D, 25.3D),
            Block.box(-3.0D, 46.0D, 16.0D, -2.0D, 48.0D, 24.3D),
            Block.box(-4.0D, 46.0D, 16.0D, -3.0D, 48.0D, 23.3D),
            Block.box(-5.0D, 46.0D, 16.0D, -4.0D, 48.0D, 22.3D),
            Block.box(-6.0D, 46.0D, 16.0D, -5.0D, 48.0D, 21.3D),
            Block.box(-7.0D, 46.0D, 16.0D, -6.0D, 48.0D, 20.3D),
            Block.box(-8.0D, 46.0D, 16.0D, -7.0D, 48.0D, 19.3D),
            Block.box(-9.0D, 46.0D, 16.0D, -8.0D, 48.0D, 18.3D),
            Block.box(-10.0D, 46.0D, 16.0D, -9.0D, 48.0D, 17.3D),

            Block.box(16.0D, 46.0D, 16.0D, 17.0D, 48.0D, 26.3D),
            Block.box(17.0D, 46.0D, 16.0D, 18.0D, 48.0D, 25.3D),
            Block.box(18.0D, 46.0D, 16.0D, 19.0D, 48.0D, 24.3D),
            Block.box(19.0D, 46.0D, 16.0D, 20.0D, 48.0D, 23.3D),
            Block.box(20.0D, 46.0D, 16.0D, 21.0D, 48.0D, 22.3D),
            Block.box(21.0D, 46.0D, 16.0D, 22.0D, 48.0D, 21.3D),
            Block.box(22.0D, 46.0D, 16.0D, 23.0D, 48.0D, 20.3D),
            Block.box(23.0D, 46.0D, 16.0D, 24.0D, 48.0D, 19.3D),
            Block.box(24.0D, 46.0D, 16.0D, 25.0D, 48.0D, 18.3D),
            Block.box(25.0D, 46.0D, 16.0D, 26.0D, 48.0D, 17.3D),

            Block.box(-1.0D, 46.0D, -10.3D, 0.0D, 48.0D, 0.0D),
            Block.box(-2.0D, 46.0D, -9.3D, -1.0D, 48.0D, 0.0D),
            Block.box(-3.0D, 46.0D, -8.3D, -2.0D, 48.0D, 0.0D),
            Block.box(-4.0D, 46.0D, -7.3D, -3.0D, 48.0D, 0.0D),
            Block.box(-5.0D, 46.0D, -6.3D, -4.0D, 48.0D, 0.0D),
            Block.box(-6.0D, 46.0D, -5.3D, -5.0D, 48.0D, 0.0D),
            Block.box(-7.0D, 46.0D, -4.3D, -6.0D, 48.0D, 0.0D),
            Block.box(-8.0D, 46.0D, -3.3D, -7.0D, 48.0D, 0.0D),
            Block.box(-9.0D, 46.0D, -2.3D, -8.0D, 48.0D, 0.0D),
            Block.box(-10.0D, 46.0D, -1.3D, -9.0D, 48.0D, 0.0D),

            Block.box(16.0D, 46.0D, -10.3D, 17.0D, 48.0D, 0.0D),
            Block.box(17.0D, 46.0D, -9.3D, 18.0D, 48.0D, 0.0D),
            Block.box(18.0D, 46.0D, -8.3D, 19.0D, 48.0D, 0.0D),
            Block.box(19.0D, 46.0D, -7.3D, 20.0D, 48.0D, 0.0D),
            Block.box(20.0D, 46.0D, -6.3D, 21.0D, 48.0D, 0.0D),
            Block.box(21.0D, 46.0D, -5.3D, 22.0D, 48.0D, 0.0D),
            Block.box(22.0D, 46.0D, -4.3D, 23.0D, 48.0D, 0.0D),
            Block.box(23.0D, 46.0D, -3.3D, 24.0D, 48.0D, 0.0D),
            Block.box(24.0D, 46.0D, -2.3D, 25.0D, 48.0D, 0.0D),
            Block.box(25.0D, 46.0D, -1.3D, 26.0D, 48.0D, 0.0D)
    );

    public static final VoxelShape SHAPE_FRAME_FRONT = Shapes.or(
            Block.box(0.0D, 0.0D, -11.3D, 16.0D, 48.0D, -9.3D),

            Block.box(-1.0D, 2.0D, -10.3D, 0.0D, 46.0D, -8.0D),
            Block.box(-2.0D, 2.0D, -9.3D, -1.0D, 46.0D, -7.0D),
            Block.box(-3.0D, 2.0D, -8.3D, -2.0D, 46.0D, -6.0D),
            Block.box(-4.0D, 2.0D, -7.3D, -3.0D, 46.0D, -5.0D),
            Block.box(-5.0D, 2.0D, -6.3D, -4.0D, 46.0D, -4.0D),
            Block.box(-6.0D, 2.0D, -5.3D, -5.0D, 46.0D, -3.0D),
            Block.box(-7.0D, 2.0D, -4.3D, -6.0D, 46.0D, -2.0D),
            Block.box(-8.0D, 2.0D, -3.3D, -7.0D, 46.0D, -1.0D),
            Block.box(-9.0D, 2.0D, -2.3D, -8.0D, 46.0D, 0.0D),
            Block.box(-10.0D, 2.0D, -1.3D, -9.0D, 46.0D, 0.0D),

            Block.box(16.0D, 2.0D, -10.3D, 17.0D, 46.0D, -8.0D),
            Block.box(17.0D, 2.0D, -9.3D, 18.0D, 46.0D, -7.0D),
            Block.box(18.0D, 2.0D, -8.3D, 19.0D, 46.0D, -6.0D),
            Block.box(19.0D, 2.0D, -7.3D, 20.0D, 46.0D, -5.0D),
            Block.box(20.0D, 2.0D, -6.3D, 21.0D, 46.0D, -4.0D),
            Block.box(21.0D, 2.0D, -5.3D, 22.0D, 46.0D, -3.0D),
            Block.box(22.0D, 2.0D, -4.3D, 23.0D, 46.0D, -2.0D),
            Block.box(23.0D, 2.0D, -3.3D, 24.0D, 46.0D, -1.0D),
            Block.box(24.0D, 2.0D, -2.3D, 25.0D, 46.0D, 0.0D),
            Block.box(25.0D, 2.0D, -1.3D, 26.0D, 46.0D, 0.0D)
    );

    public static final VoxelShape INTERACTION_SHAPE = Block.box(-11.3D, 0.0D, -11.3D, 27.3D, 48.0D, 27.3D);

    public static final VoxelShape SHAPE_FRAME = Shapes.or(SHAPE_FRAME_LEFT, SHAPE_FRAME_RIGHT, SHAPE_FRAME_BACK, SHAPE_FRAME_BOTTOM, SHAPE_FRAME_TOP);
    public static final VoxelShape SHAPE_DOOR = Shapes.or(SHAPE_FRAME_FRONT);
    public static final VoxelShape SHAPE_COLLISION_CLOSED = Shapes.or(SHAPE_FRAME, SHAPE_DOOR);

    private final SoundEvent open, close;

    private final VoxelShape shapeFrame;
    private final VoxelShape shapeCollisionClosed;

    public StasisChamber(SoundEvent open, SoundEvent close) {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 9.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SECTION, ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE)
                .setValue(OPEN, Boolean.FALSE));
        this.open = open;
        this.close = close;

        this.shapeFrame = SHAPE_FRAME;
        this.shapeCollisionClosed = SHAPE_COLLISION_CLOSED;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = shapeFrame;

        if (state.getValue(OPEN)) {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), shapeFrame);
        }
        else {
            shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), shapeCollisionClosed);
        }

        var offset = state.getValue(SECTION).getOffset(state.getValue(FACING), ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE);
        return shape.move(offset.getX(), offset.getY(), offset.getZ());
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state, level, pos);
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
        builder.add(FACING, SECTION, OPEN);
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
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos blockpos, BlockState blockState) {
        if (blockState.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE)
            super.spawnDestroyParticles(level, player, blockpos, blockState);
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
            NetworkHooks.openGui(serverPlayer, getMenuProvider(beState, level, bePos), extra -> {
                extra.writeBlockPos(bePos);
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

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction.Axis> allCheckOrAxis) {
        if (allCheckOrAxis.left().isPresent() && !allCheckOrAxis.left().get() && state.getValue(SECTION) == ThreeXThreeSection.MIDDLE_BOTTOM_MIDDLE)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrAxis.left().isPresent() && allCheckOrAxis.left().get() ? Arrays.stream(ThreeXThreeSection.values()).toList() : thisSect.getOtherValues()) {
            if (allCheckOrAxis.right().isPresent()) {
                if (!thisSect.isOnAxis(sect, state.getValue(FACING), allCheckOrAxis.right().get()))
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
        if (!this.canSurvive(state, level, pos, Either.right(direction.getAxis())))
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
            level.gameEvent(GameEvent.BLOCK_OPEN, pos);
        }
        level.playSound(null, pos, open, SoundSource.BLOCKS, 1, 1);
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
            level.gameEvent(GameEvent.BLOCK_CLOSE, pos);
        }
        level.playSound(null, pos, close, SoundSource.BLOCKS, 1, 1);
        return true;
    }

    @Override
    public boolean isOpen(BlockState state, Level level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    @Override
    public AABB getDetectionSize(BlockState state, Level level, BlockPos pos) {
        return new AABB(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), ThreeXThreeSection.CENTER));
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        if (this.stateHasBlockEntity(state))
            return level.getBlockEntity(pos, ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);
        return null;
    }

    private static final Vec3 SIT_OFFSET = new Vec3(0.0D, -0.9D, 0.0D);

    @Override
    public Vec3 getSitOffset(BlockGetter level, BlockState state, BlockPos pos) {
        return SIT_OFFSET;
    }

    public static boolean isEntityStabilized(LivingEntity livingEntity) {
        if (livingEntity.vehicle instanceof SeatEntity seatEntity) {
            return livingEntity.level.getBlockEntity(seatEntity.getAttachedBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get())
                    .map(StasisChamberBlockEntity::isStabilized)
                    .orElse(false);
        }

        return false;
    }

    public static boolean isEntityCaptured(LivingEntity livingEntity) {
        if (livingEntity.vehicle instanceof SeatEntity seatEntity) {
            return livingEntity.level.getBlockEntity(seatEntity.getAttachedBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get())
                    .isPresent();
        }

        return false;
    }
}
