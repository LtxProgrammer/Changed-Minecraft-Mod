package net.ltxprogrammer.changed.entity.animation;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.minecraft.world.entity.LivingEntity;

public class StasisAnimationParameters implements AnimationParameters {
    public static StasisAnimationParameters INSTANCE = new StasisAnimationParameters();

    public static Codec<StasisAnimationParameters> CODEC = Codec.unit(() -> INSTANCE);

    private StasisAnimationParameters() {}

    @Override
    public AnimationAssociation.Match matchesAssociation(AnimationAssociation association) {
        return AnimationAssociation.Match.DEFAULT;
    }

    @Override
    public boolean shouldEndAnimation(LivingEntity livingEntity, float totalTime) {
        return !StasisChamber.isEntityCaptured(livingEntity) && totalTime > 0.2f;
    }

    @Override
    public boolean shouldLoop(LivingEntity livingEntity, float totalTime) {
        return true;
    }
}
