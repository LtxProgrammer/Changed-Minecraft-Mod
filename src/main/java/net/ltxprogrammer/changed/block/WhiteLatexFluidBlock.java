package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class WhiteLatexFluidBlock extends AbstractLatexFluidBlock implements WhiteLatexTransportInterface {
    public WhiteLatexFluidBlock() {
        super(ChangedFluids.WHITE_LATEX, BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).replaceable().strength(100f));
    }

    @Override
    public @NotNull LatexCoverState getLatexCoverState(BlockState blockState) {
        return blockState.getValue(GROUNDED) ? ChangedLatexTypes.WHITE_LATEX.get().sourceCoverState() : ChangedLatexTypes.NONE.get().defaultCoverState();
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (LatexType.getEntityLatexType(player) == ChangedLatexTypes.WHITE_LATEX.get() &&
                /*player.isShiftKeyDown() && */player.getItemInHand(player.getUsedItemHand()).isEmpty() && !WhiteLatexTransportInterface.isEntityInWhiteLatex(player)) { // Empty-handed RMB
            if (pos.distSqr(new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ())) > 4.0)
                return super.use(state, level, pos, player, hand, hitResult);

            WhiteLatexTransportInterface.entityEnterLatex(player, pos);
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (ChangedLatexTypes.WHITE_LATEX.get().isHostileTo(LatexType.getEntityLatexType(entity)))
            entity.hurt(ChangedDamageSources.WHITE_LATEX.source(entity.level().registryAccess()), 3.0f);
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
}
