package net.ltxprogrammer.changed.entity.animation;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.minecraft.world.entity.LivingEntity;

public class StunAnimationParameters implements AnimationParameters {
    public static StunAnimationParameters INSTANCE = new StunAnimationParameters();

    public static Codec<StunAnimationParameters> CODEC = Codec.unit(() -> INSTANCE);

    private StunAnimationParameters() {}

    @Override
    public AnimationAssociation.Match matchesAssociation(AnimationAssociation association) {
        return AnimationAssociation.Match.DEFAULT;
    }

    @Override
    public boolean shouldEndAnimation(LivingEntity livingEntity, float totalTime) {
        return !livingEntity.hasEffect(ChangedEffects.SHOCK) && totalTime > 0.2f;
    }

    @Override
    public boolean shouldLoop(LivingEntity livingEntity, float totalTime) {
        return true;
    }
}
