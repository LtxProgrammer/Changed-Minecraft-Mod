package net.ltxprogrammer.changed.ability.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectNodeEffect extends NodeEffect {
    public static final Codec<MobEffectInstance> MOB_EFFECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
            Codec.INT.fieldOf("duration").orElse(600).forGetter(MobEffectInstance::getDuration),
            Codec.INT.fieldOf("amplifier").orElse(0).forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.fieldOf("hideParticles").orElse(false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.fieldOf("visible").orElse(true).forGetter(MobEffectInstance::isVisible),
            Codec.BOOL.fieldOf("showIcon").orElse(true).forGetter(MobEffectInstance::showIcon)
    ).apply(instance, MobEffectInstance::new));

    public static final Codec<MobEffectNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            MOB_EFFECT_CODEC.fieldOf("mobEffect").forGetter(node -> node.mobEffect)
    ).apply(builder, MobEffectNodeEffect::new));

    public final AbstractCondition condition;
    public final MobEffectInstance mobEffect;

    public MobEffectNodeEffect(AbstractCondition condition, MobEffectInstance mobEffect) {
        this.condition = condition;
        this.mobEffect = mobEffect;
    }

    @Override
    public void applyEffect(AbilityCounter counter) {
        if (condition.test(counter.entity))
            counter.variantInstance.getHost().addEffect(new MobEffectInstance(mobEffect));
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }
}
