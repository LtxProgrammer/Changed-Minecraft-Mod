package net.ltxprogrammer.changed.mixin.compatibility.PlayerAnimator;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ChangedEntity.class, remap = false)
@RequiredMods("playeranimator")
public abstract class ChangedEntityMixin extends Monster implements EntityShape.Provider, IAnimatedPlayer {
    @Shadow private @Nullable Player underlyingPlayer;

    // Partially implement PlayerAnimator's code to ChangedEntity, and forward API calls to underlying player
    @Unique
    private final AnimationStack animationStack = new AnimationStack();
    @Unique
    private final AnimationApplier animationApplier = new AnimationApplier(animationStack);

    protected ChangedEntityMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Override
    public AnimationStack getAnimationStack() {
        if (underlyingPlayer instanceof IAnimatedPlayer underlyingAnimatedPlayer)
            return underlyingAnimatedPlayer.getAnimationStack();
        return animationStack;
    }

    @Override
    public AnimationApplier playerAnimator_getAnimation() {
        if (underlyingPlayer instanceof IAnimatedPlayer underlyingAnimatedPlayer)
            return underlyingAnimatedPlayer.playerAnimator_getAnimation();
        return animationApplier;
    }

    @Override
    public @Nullable IAnimation playerAnimator_getAnimation(@NotNull ResourceLocation resourceLocation) {
        if (underlyingPlayer instanceof IAnimatedPlayer underlyingAnimatedPlayer)
            return underlyingAnimatedPlayer.playerAnimator_getAnimation(resourceLocation);
        return null;
    }

    @Override
    public @Nullable IAnimation playerAnimator_setAnimation(@NotNull ResourceLocation resourceLocation, @Nullable IAnimation iAnimation) {
        if (underlyingPlayer instanceof IAnimatedPlayer underlyingAnimatedPlayer)
            return underlyingAnimatedPlayer.playerAnimator_setAnimation(resourceLocation, iAnimation);
        return null;
    }
}
