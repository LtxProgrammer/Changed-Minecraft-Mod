package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class WhiteLatexBlock extends AbstractLatexBlock implements WhiteLatexTransportInterface {
    public WhiteLatexBlock(Properties p_49795_) {
        super(p_49795_.noOcclusion(), ChangedLatexTypes.WHITE_LATEX, ChangedItems.WHITE_LATEX_GOO);
    }

    @Override
    public @NotNull LatexCoverState getLatexCoverState(BlockState blockState) {
        return ChangedLatexTypes.WHITE_LATEX.get().sourceCoverState();
    }

    public static final List<Supplier<? extends WhiteLatexFlora>> SMALL_FLORA = List.of(
            ChangedBlocks.WHITE_LATEX_SWIRL,
            ChangedBlocks.WHITE_LATEX_SQUIGGLE,
            ChangedBlocks.WHITE_LATEX_STUMP
    );

    public static final List<Supplier<? extends WhiteLatexFlora>> LARGE_FLORA = List.of();

    public boolean skipRendering(BlockState thisState, BlockState otherState, Direction direction) {
        return otherState.is(this) ? true : super.skipRendering(thisState, otherState, direction);
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.empty();
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ecc) {
            if (ecc.getEntity() instanceof LivingEntity le) {
                if (WhiteLatexTransportInterface.isEntityInWhiteLatex(le))
                    return Shapes.empty();
            }
        }

        return Shapes.block();
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos blockPos, Entity entity, float distance) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            super.fallOn(level, state, blockPos, entity, distance);
            return;
        }

        if (LatexType.getEntityLatexType(livingEntity) == ChangedLatexTypes.WHITE_LATEX.get() && distance > 3.0f) {
            if (livingEntity instanceof Player player)
                WhiteLatexTransportInterface.entityEnterLatex(player, blockPos);
        } else {
            super.fallOn(level, state, blockPos, entity, distance);
        }
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (LatexType.getEntityLatexType(player) == ChangedLatexTypes.WHITE_LATEX.get() &&
                /*player.isShiftKeyDown() && */player.getItemInHand(player.getUsedItemHand()).isEmpty() && !WhiteLatexTransportInterface.isEntityInWhiteLatex(player)) { // Empty-handed RMB
            if (pos.distSqr(new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ())) > 4.0)
                return super.use(state, level, pos, player, hand, hitResult);

            WhiteLatexTransportInterface.entityEnterLatex(player, pos);
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    public static boolean targetNearby(ServerLevel level, BlockPos blockPos) {
        AtomicBoolean isTargetNearby = new AtomicBoolean(false);
        level.getEntities().get(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPos).inflate(6), livingEntity -> {
            if (isTargetNearby.get())
                return AbortableIterationConsumer.Continuation.ABORT;

            var latexType = LatexType.getEntityLatexType(livingEntity);
            if (latexType != null && latexType.isHostileTo(ChangedLatexTypes.WHITE_LATEX.get())) {
                isTargetNearby.set(true);
                return AbortableIterationConsumer.Continuation.ABORT;
            }

            if (ChangedFusions.INSTANCE.getFusionsFor(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), livingEntity.getClass()).findAny().isPresent()) {
                isTargetNearby.set(true);
                return AbortableIterationConsumer.Continuation.ABORT;
            }

            var latexVariant = TransfurVariant.getEntityVariant(livingEntity);
            if (latexVariant != null && ChangedFusions.INSTANCE.getFusionsFor(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), latexVariant).findAny().isPresent()) {
                isTargetNearby.set(true);
                return AbortableIterationConsumer.Continuation.ABORT;
            }

            if (livingEntity instanceof Player player && !player.isSpectator() && !ProcessTransfur.isPlayerTransfurred(player)) {
                isTargetNearby.set(true);
                return AbortableIterationConsumer.Continuation.ABORT;
            }

            return AbortableIterationConsumer.Continuation.CONTINUE;
        });
        return isTargetNearby.getAcquire();
    }

    public static final List<Supplier<? extends WhiteLatexPillar>> PILLAR = List.of(
            ChangedBlocks.WHITE_LATEX_PILLAR
    );
}
