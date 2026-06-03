package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantFeature;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class EnableFeatureNodeEffect extends NodeEffect {
    public static final Codec<EnableFeatureNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            ChangedRegistry.TRANSFUR_VARIANT_FEATURES.get().getCodec().fieldOf("feature").forGetter(node -> node.feature),
            Codec.DOUBLE.fieldOf("factor").orElse(1.0).forGetter(node -> node.factor)
    ).apply(builder, EnableFeatureNodeEffect::new));

    public final AbstractCondition condition;
    public final TransfurVariantFeature feature;
    public final double factor;

    public EnableFeatureNodeEffect(AbstractCondition condition, TransfurVariantFeature feature, double factor) {
        this.condition = condition;
        this.feature = feature;
        this.factor = factor;
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

    @Nullable
    @Override
    protected NodeEffect createClientNodeEffect() {
        return new EnableFeatureNodeEffect(TrueCondition.INSTANCE, this.feature, this.factor);
    }

    @Override
    public void buildDescription(Consumer<Component> componentConsumer, boolean negate) {
        super.buildDescription(componentConsumer, negate);

        double d0 = this.factor;
        if (negate)
            d0 = -d0;

        double d1 = d0;

        if (d0 > 0.0D) {
            componentConsumer.accept(Component.translatable("attribute.modifier.plus.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), feature.getDisplayName()).withStyle(ChatFormatting.BLUE));
        } else if (d0 < 0.0D) {
            d1 *= -1.0D;
            componentConsumer.accept(Component.translatable("attribute.modifier.take.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), feature.getDisplayName()).withStyle(ChatFormatting.RED));
        }
    }
}
