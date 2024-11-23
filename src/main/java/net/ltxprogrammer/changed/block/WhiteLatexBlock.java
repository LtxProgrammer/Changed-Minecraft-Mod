package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.LatexCoveredBlocks;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WhiteLatexBlock extends AbstractLatexBlock implements WhiteLatexTransportInterface {
    public WhiteLatexBlock(Properties p_49795_) {
        super(p_49795_.noOcclusion(), LatexType.WHITE_LATEX, ChangedItems.WHITE_LATEX_GOO);
    }

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
                if (le.fallDistance > 3.0f)
                    return Shapes.empty();
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

        TransfurVariant<?> variant = TransfurVariant.getEntityVariant(livingEntity);
        if (variant != null && variant.getLatexType() == LatexType.WHITE_LATEX && distance > 3.0f) {
            if (livingEntity instanceof Player player)
                WhiteLatexTransportInterface.entityEnterLatex(player, blockPos);
        } else {
            super.fallOn(level, state, blockPos, entity, distance);
        }
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant != null && variant.getLatexType() == LatexType.WHITE_LATEX &&
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
                return; // Early out

            var latexType = LatexType.getEntityLatexType(livingEntity);
            if (latexType != null && latexType.isHostileTo(LatexType.WHITE_LATEX)) {
                isTargetNearby.set(true);
                return;
            }

            if (ChangedFusions.INSTANCE.getFusionsFor(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), livingEntity.getClass()).findAny().isPresent()) {
                isTargetNearby.set(true);
                return;
            }

            var latexVariant = TransfurVariant.getEntityVariant(livingEntity);
            if (latexVariant != null && ChangedFusions.INSTANCE.getFusionsFor(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), latexVariant).findAny().isPresent()) {
                isTargetNearby.set(true);
                return;
            }

            if (livingEntity instanceof Player player && !player.isSpectator() && !ProcessTransfur.isPlayerTransfurred(player)) {
                isTargetNearby.set(true);
                return;
            }
        });
        return isTargetNearby.getAcquire();
    }

    @Override
    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(1000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;
        if (!targetNearby(level, position))
            return;

        BlockPos above = position.above();
        if (level.getBlockState(above).is(Blocks.AIR) && level.getBlockState(above.above()).is(Blocks.AIR)) {
            ChangedEntities.PURE_WHITE_LATEX_WOLF.get().spawn(level, null, null, null, above, MobSpawnType.NATURAL, true, true);
        }
    }

    private static final List<Supplier<? extends WhiteLatexPillar>> PILLAR = List.of(
            ChangedBlocks.WHITE_LATEX_PILLAR
    );

    @SubscribeEvent
    public static void onLatexCover(LatexCoveredBlocks.CoveringBlockEvent event) {
        if (event.latexType != LatexType.WHITE_LATEX)
            return;

        if (event.originalState.is(Blocks.TALL_GRASS) || event.originalState.is(Blocks.LARGE_FERN) || event.originalState.is(BlockTags.TALL_FLOWERS)) {
            var pillar = PILLAR.get(event.level.random.nextInt(PILLAR.size()));
            switch (event.originalState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                case UPPER -> {
                    event.level.setBlockAndUpdate(event.blockPos.below(), pillar.get().defaultBlockState()
                            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                    event.setPlannedState(pillar.get().defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                }

                case LOWER -> {
                    event.level.setBlockAndUpdate(event.blockPos.above(), pillar.get().defaultBlockState()
                            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                    event.setPlannedState(pillar.get().defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                }
            }
        }
    }
}
