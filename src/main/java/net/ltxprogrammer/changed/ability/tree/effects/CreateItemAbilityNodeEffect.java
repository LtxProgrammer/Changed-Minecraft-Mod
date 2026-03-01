package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleCreateItemAbility;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class CreateItemAbilityNodeEffect extends NodeEffect {
    public final AbstractCondition condition;
    public final ItemStack itemStack;
    public final float exhaustion;
    public final float minimumHunger;

    public final SimpleCreateItemAbility resolvedAbility;

    public static final Codec<CreateItemAbilityNodeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(effect -> effect.condition),
            ItemStack.CODEC.fieldOf("itemStack").forGetter(effect -> effect.itemStack),
            Codec.FLOAT.fieldOf("exhaustion").forGetter(effect -> effect.exhaustion),
            Codec.FLOAT.fieldOf("minimumHunger").forGetter(effect -> effect.minimumHunger)
    ).apply(instance, CreateItemAbilityNodeEffect::new));

    public CreateItemAbilityNodeEffect(AbstractCondition condition, ItemStack itemStack, float exhaustion, float minimumHunger) {
        this.condition = condition;
        this.itemStack = itemStack;
        this.exhaustion = exhaustion;
        this.minimumHunger = minimumHunger;

        this.resolvedAbility = new SimpleCreateItemAbility(() -> itemStack, exhaustion, minimumHunger) {
            @Override
            public boolean canUse(IAbstractChangedEntity entity) {
                return super.canUse(entity) && condition.test(entity);
            }
        };
    }

    @Override
    public void gatherActiveEffects(IAbstractChangedEntity entity, Consumer<NodeEffect> sink) {

    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }
}
