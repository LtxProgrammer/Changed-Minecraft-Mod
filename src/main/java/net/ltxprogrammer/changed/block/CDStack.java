package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.CDStackBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CDStack extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty DISKS = IntegerProperty.create("disks", 1, 16);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape ONE_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
    public static final VoxelShape TWO_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D);
    public static final VoxelShape THREE_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);
    public static final VoxelShape FOUR_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 4.0D, 13.0D);
    public static final VoxelShape FIVE_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 5.0D, 13.0D);
    public static final VoxelShape SIX_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    public static final VoxelShape SEVEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 7.0D, 13.0D);
    public static final VoxelShape EIGHT_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    public static final VoxelShape NINE_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D);
    public static final VoxelShape TEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);
    public static final VoxelShape ELEVEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 11.0D, 13.0D);
    public static final VoxelShape TWELVE_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 12.0D, 13.0D);
    public static final VoxelShape THIRTEEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 13.0D, 13.0D);
    public static final VoxelShape FOURTEEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 14.0D, 13.0D);
    public static final VoxelShape FIFTEEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 15.0D, 13.0D);
    public static final VoxelShape SIXTEEN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);

    public CDStack(Properties properties) {
        super(properties.offsetType(OffsetType.XZ).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(DISKS, 1).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DISKS, WATERLOGGED);
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, entity, itemStack);
    }

    public RenderShape getRenderShape(BlockState state) {
        return super.getRenderShape(state);
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Vec3 vec3 = state.getOffset(level, pos);
        return (switch (state.getValue(DISKS)) {
            case 1 -> ONE_AABB;
            case 2 -> TWO_AABB;
            case 3 -> THREE_AABB;
            case 4 -> FOUR_AABB;
            case 5 -> FIVE_AABB;
            case 6 -> SIX_AABB;
            case 7 -> SEVEN_AABB;
            case 8 -> EIGHT_AABB;
            case 9 -> NINE_AABB;
            case 10 -> TEN_AABB;
            case 11 -> ELEVEN_AABB;
            case 12 -> TWELVE_AABB;
            case 13 -> THIRTEEN_AABB;
            case 14 -> FOURTEEN_AABB;
            case 15 -> FIFTEEN_AABB;
            case 16 -> SIXTEEN_AABB;
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(DISKS));
        }).move(vec3.x, vec3.y, vec3.z);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
        return getInteractionShape(state, getter, pos);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var belowState = level.getBlockState(pos.below());
        if (belowState.is(this) && belowState.getValue(DISKS) == 16)
            return true;
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(flag));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CDStackBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        AtomicReference<InteractionResult> result = new AtomicReference<>(InteractionResult.PASS);
        ItemStack itemInHand = player.getItemInHand(hand);

        if (itemInHand.is(ChangedItems.COMPACT_DISC.get()) && blockState.getValue(DISKS) == 16)
            return super.use(blockState, level, blockPos, player, hand, blockHitResult);

        if (level.isClientSide) {
            if (itemInHand.is(ChangedItems.COMPACT_DISC.get()) && blockState.getValue(DISKS) < 16) {
                BlockState nextState = blockState.cycle(DISKS);

                CollisionContext collisioncontext = CollisionContext.of(player);
                if (!level.isUnobstructed(nextState, blockPos, collisioncontext)) {
                    return InteractionResult.FAIL;
                }

                SoundType soundtype = nextState.getSoundType(level, blockPos, player);
                level.playSound(player, blockPos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, nextState));
            }

            return InteractionResult.sidedSuccess(true);
        } else {
            level.getBlockEntity(blockPos, ChangedBlockEntities.CD_STACK.get()).ifPresent(blockEntity -> {
                if (blockEntity.push(itemInHand)) {
                    BlockState nextState = blockState.setValue(CDStack.DISKS, blockEntity.size());

                    CollisionContext collisioncontext = CollisionContext.of(player);
                    if (!level.isUnobstructed(nextState, blockPos, collisioncontext)) {
                        blockEntity.pop();
                        result.set(InteractionResult.FAIL);
                        return;
                    }

                    level.setBlock(blockPos, nextState, 11);

                    SoundType soundtype = nextState.getSoundType(level, blockPos, player);
                    level.playSound(player, blockPos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, nextState));

                    if (!player.getAbilities().instabuild) {
                        itemInHand.shrink(1);
                    }

                    result.set(InteractionResult.sidedSuccess(false));
                    return;
                }

                ItemStack itemStack = blockEntity.pop();
                if (itemStack != null) {
                    int count = blockEntity.size();
                    BlockState nextState = count <= 0 ? Blocks.AIR.defaultBlockState() : blockState.setValue(CDStack.DISKS, count);

                    level.setBlock(blockPos, nextState, 11);

                    if (!player.addItem(itemStack))
                        Block.popResource(level, blockPos, itemStack);

                    result.set(InteractionResult.sidedSuccess(false));
                }
            });
        }

        return result.getAcquire() == InteractionResult.PASS ? super.use(blockState, level, blockPos, player, hand, blockHitResult) : result.getAcquire();
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder lootParams) {
        return ((CDStackBlockEntity) lootParams.getParameter(LootContextParams.BLOCK_ENTITY)).getDrops();
    }
}
