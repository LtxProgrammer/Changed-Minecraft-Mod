package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class UnlockActiveAbilityNodeEffect extends NodeEffect {
    public final AbstractAbility<?> ability;
    public final int level;

    public static final Codec<UnlockActiveAbilityNodeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChangedRegistry.ABILITY.get().getCodec().fieldOf("ability").forGetter(effect -> effect.ability),
            Codec.INT.fieldOf("level").orElse(0).forGetter(effect -> effect.level)
    ).apply(instance, UnlockActiveAbilityNodeEffect::new));

    public UnlockActiveAbilityNodeEffect(AbstractAbility<?> ability, int level) {
        this.ability = ability;
        this.level = level;
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

        var abilityName = Component.translatable("ability." + ChangedRegistry.ABILITY.getKey(ability).toString().replace(':', '.'));;

        var levelText = Component.translatable("enchantment.level." + (this.level + 1));
        if (!negate) {
            componentConsumer.accept(Component.translatable("text.changed.ability_tree.node.active_ability.give", abilityName, levelText).withStyle(ChatFormatting.BLUE));
        } else {
            componentConsumer.accept(Component.translatable("text.changed.ability_tree.node.active_ability.take", abilityName, levelText).withStyle(ChatFormatting.RED));
        }
    }
}
