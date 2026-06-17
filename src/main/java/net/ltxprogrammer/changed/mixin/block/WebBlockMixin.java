package net.ltxprogrammer.changed.mixin.block;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WebBlock.class)
public abstract class WebBlockMixin extends Block {
    public WebBlockMixin(Properties properties) {
        super(properties);
    }

    @WrapMethod(method = "entityInside")
    public void changed$allowPlayerPassage(BlockState state, Level level, BlockPos pos, Entity entity, Operation<Void> original) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant == null) {
            original.call(state, level, pos, entity);
            return;
        }

        if (variant.hasFeature(ChangedVariantFeatures.CLIMB_COBWEB.get())) {
            entity.resetFallDistance();
        } else {
            original.call(state, level, pos, entity);
        }
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant == null)
            return super.isLadder(state, level, pos, entity);
        return variant.hasFeature(ChangedVariantFeatures.CLIMB_COBWEB.get()) ||
                super.isLadder(state, level, pos, entity);
    }
}
