package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class WhiteLatexFluidBlock extends AbstractLatexFluidBlock implements WhiteLatexTransportInterface {
    public WhiteLatexFluidBlock() {
        super(() -> (FlowingFluid)ChangedFluids.WHITE_LATEX.get(), BlockBehaviour.Properties.of(Material.WATER).strength(100f));
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

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof LatexEntity latexEntity) {
            if (latexEntity.getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                latexEntity.hurt(ChangedDamageSources.WHITE_LATEX, 3.0f);
        }
        ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(entity), (player, variantInstance) -> {
            if (variantInstance.getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                player.hurt(ChangedDamageSources.WHITE_LATEX, 3.0f);
        });
    }
}
