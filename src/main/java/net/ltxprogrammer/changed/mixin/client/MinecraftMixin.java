package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public Entity cameraEntity;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void isEntityMovingOnWhiteLatex(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (!(entity instanceof LivingEntity livingEntity))
            return;
        if (this.cameraEntity == null)
            return;
        if (LatexType.getEntityLatexType(this.cameraEntity) != LatexType.WHITE_LATEX)
            return;
        if (LatexType.getEntityLatexType(livingEntity) == LatexType.WHITE_LATEX)
            return;

        BlockState standing = livingEntity.level.getBlockState(livingEntity.blockPosition().below());
        if (standing == null || standing.isAir())
            return;
        if (AbstractLatexBlock.getLatexed(standing) == LatexType.WHITE_LATEX)
            callback.setReturnValue(true);
    }
}
