package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Consumer;

public class AttributeModifierNodeEffect extends NodeEffect {
    public static final Codec<AttributeModifierNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            ForgeRegistries.ATTRIBUTES.getCodec().fieldOf("attribute").forGetter(node -> node.attribute),
            Method.CODEC.fieldOf("method").orElse(Method.MULTIPLY_BASE).forGetter(condition -> condition.method),
            Codec.DOUBLE.fieldOf("factor").forGetter(node -> node.factor)
    ).apply(builder, AttributeModifierNodeEffect::new));

    public enum Method implements StringRepresentable {
        MULTIPLY_BASE("multiply_base"),
        ADD("add");

        public static Codec<Method> CODEC = Codec.STRING.comapFlatMap(Method::fromSerial, Method::getSerializedName);

        public final String serialName;

        Method(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        public static DataResult<Method> fromSerial(String name) {
            return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                    .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> name + " is not a valid Method"));
        }

        public int toValue() {
            return this == ADD ? 0 : 1;
        }
    }

    public final AbstractCondition condition;
    public final Attribute attribute;
    public final Method method;
    public final double factor;

    public AttributeModifierNodeEffect(AbstractCondition condition, Attribute attribute, Method method, double factor) {
        this.condition = condition;
        this.attribute = attribute;
        this.method = method;
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

    @Override
    public void buildDescription(Consumer<Component> componentConsumer, boolean negate) {
        super.buildDescription(componentConsumer, negate);

        double d0 = this.factor;
        if (negate)
            d0 = -d0;

        double d1;
        if (method != Method.MULTIPLY_BASE) {
            if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
                d1 = d0 * 10.0D;
            } else {
                d1 = d0;
            }
        } else {
            d1 = d0 * 100.0D;
        }

        if (d0 > 0.0D) {
            componentConsumer.accept(Component.translatable("attribute.modifier.plus." + method.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.BLUE));
        } else if (d0 < 0.0D) {
            d1 *= -1.0D;
            componentConsumer.accept(Component.translatable("attribute.modifier.take." + method.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.RED));
        }
    }
}
