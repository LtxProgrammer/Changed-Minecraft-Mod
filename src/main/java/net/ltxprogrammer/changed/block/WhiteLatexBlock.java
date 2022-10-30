package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class WhiteLatexBlock extends AbstractLatexBlock implements WhiteLatexTransportInterface {
    public WhiteLatexBlock(Properties p_49795_) {
        super(p_49795_, LatexType.WHITE_LATEX, ChangedItems.WHITE_LATEX_GOO);
    }

    public boolean skipRendering(BlockState p_53972_, BlockState p_53973_, Direction p_53974_) {
        return p_53973_.is(this) || (p_53973_.getProperties().contains(COVERED) && p_53973_.getValue(COVERED) == LatexType.WHITE_LATEX) ? true : super.skipRendering(p_53972_, p_53973_, p_53974_);
    }

    /*public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.empty();
    }*/

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
        LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
        if (variant != null && variant.getLatexType() == LatexType.WHITE_LATEX &&
                /*player.isShiftKeyDown() && */player.getItemInHand(player.getUsedItemHand()).isEmpty() && !WhiteLatexTransportInterface.isEntityInWhiteLatex(player)) { // Empty-handed RMB
            if (pos.distSqr(new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ())) > 4.0)
                return super.use(state, level, pos, player, hand, hitResult);

            WhiteLatexTransportInterface.entityEnterLatex(player, pos);
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);

        if (random.nextInt(250) > 0)
            return;

        BlockPos above = position.above();
        if (level.getBlockState(above).is(Blocks.AIR) && level.getBlockState(above.above()).is(Blocks.AIR)) {
            ChangedEntities.WHITE_LATEX_WOLF.get().spawn(level, null, null, null, above, MobSpawnType.NATURAL, true, true);
        }
    }
}
