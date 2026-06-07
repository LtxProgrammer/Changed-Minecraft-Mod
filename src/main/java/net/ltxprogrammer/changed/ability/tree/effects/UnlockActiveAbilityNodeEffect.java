package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class UnlockActiveAbilityNodeEffect extends NodeEffect {
    public final AbstractAbility<?> ability;
    public final int amplifier;

    public static final Codec<UnlockActiveAbilityNodeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChangedRegistry.ABILITY.get().getCodec().fieldOf("ability").forGetter(effect -> effect.ability),
            Codec.INT.fieldOf("amplifier").orElse(0).forGetter(effect -> effect.amplifier)
    ).apply(instance, UnlockActiveAbilityNodeEffect::new));

    public UnlockActiveAbilityNodeEffect(AbstractAbility<?> ability, int amplifier) {
        this.ability = ability;
        this.amplifier = amplifier;
    }

    @Override
    protected @Nullable NodeEffect createClientNodeEffect() {
        return this;
    }

    @Override
    public void gatherActiveEffects(IAbstractChangedEntity entity, Consumer<NodeEffect> sink) {
        sink.accept(this);
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void buildDescription(Consumer<Component> componentConsumer, boolean negate) {
        super.buildDescription(componentConsumer, negate);

        double d0 = this.amplifier;
        if (negate)
            d0 = -d0;

        double d1 = d0;

        var abilityName = Component.translatable("ability." + ChangedRegistry.ABILITY.getKey(ability).toString().replace(':', '.'));;

        if (d0 > 0.0D) {
            componentConsumer.accept(Component.translatable("attribute.modifier.plus.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), abilityName).withStyle(ChatFormatting.BLUE));
        } else if (d0 < 0.0D) {
            d1 *= -1.0D;
            componentConsumer.accept(Component.translatable("attribute.modifier.take.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), abilityName).withStyle(ChatFormatting.RED));
        }
    }
}
