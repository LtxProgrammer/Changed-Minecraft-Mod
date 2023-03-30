package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.AbstractLatexGoo;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
        super(p_49795_, LatexType.WHITE_LATEX, ChangedItems.WHITE_LATEX_GOO);
    }

    public boolean skipRendering(BlockState p_53972_, BlockState p_53973_, Direction p_53974_) {
        return p_53973_.is(this) ? true : super.skipRendering(p_53972_, p_53973_, p_53974_);
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
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

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        LatexVariantInstance<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
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

            if (!LatexVariant.getFusionCompatible(LatexVariant.WHITE_LATEX_WOLF, livingEntity.getClass()).isEmpty()) {
                isTargetNearby.set(true);
                return;
            }

            var latexVariant = LatexVariant.getEntityVariant(livingEntity);
            if (latexVariant != null && !LatexVariant.getFusionCompatible(LatexVariant.WHITE_LATEX_WOLF, latexVariant).isEmpty()) {
                isTargetNearby.set(true);
                return;
            }

            if (livingEntity instanceof Player player && !player.isSpectator() && !ProcessTransfur.isPlayerLatex(player)) {
                isTargetNearby.set(true);
                return;
            }
        });
        return isTargetNearby.getAcquire();
    }

    @Override
    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(20000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;
        if (!targetNearby(level, position))
            return;

        BlockPos above = position.above();
        if (level.getBlockState(above).is(Blocks.AIR) && level.getBlockState(above.above()).is(Blocks.AIR)) {
            ChangedEntities.WHITE_LATEX_WOLF.get().spawn(level, null, null, null, above, MobSpawnType.NATURAL, true, true);
        }
    }

    private static final List<Supplier<? extends WhiteLatexPillar>> PILLAR = List.of(
            ChangedBlocks.WHITE_LATEX_PILLAR
    );

    @SubscribeEvent
    public static void onLatexCover(AbstractLatexGoo.CoveringBlockEvent event) {
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
