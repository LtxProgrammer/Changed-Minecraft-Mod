package net.ltxprogrammer.changed.ability.tree.effects;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    public void gatherActiveEffects(IAbstractChangedEntity entity, Consumer<NodeEffect> sink) {
        if (condition.test(entity))
            sink.accept(this);
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void buildDescription(Consumer<Component> componentConsumer, boolean negate) {
        super.buildDescription(componentConsumer, negate);

        MutableComponent mutablecomponent = Component.translatable(mobEffect.getDescriptionId());
        MobEffect mobeffect = mobEffect.getEffect();

        if (mobEffect.getAmplifier() > 0) {
            mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobEffect.getAmplifier()));
        }

        if (!mobEffect.endsWithin(20)) {
            mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobEffect, 1.0f));
        }

        MobEffectCategory mobEffectCategory = mobeffect.getCategory();
        if (negate) {
            mobEffectCategory = switch (mobEffectCategory) {
                case HARMFUL -> MobEffectCategory.BENEFICIAL;
                case BENEFICIAL -> MobEffectCategory.HARMFUL;
                default -> mobEffectCategory;
            };
        }

        if (!negate)
            componentConsumer.accept(Component.translatable("text.changed.ability_tree.node.mob_effect.plus", mutablecomponent).withStyle(mobEffectCategory.getTooltipFormatting()));
        else
            componentConsumer.accept(Component.translatable("text.changed.ability_tree.node.mob_effect.take", mutablecomponent).withStyle(mobEffectCategory.getTooltipFormatting()));
    }
}
