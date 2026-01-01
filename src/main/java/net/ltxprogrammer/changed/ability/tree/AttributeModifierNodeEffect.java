package net.ltxprogrammer.changed.ability.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class AttributeModifierNodeEffect extends AbilityTree.NodeEffect {
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
    public void applyEffect(AbilityCounter counter) {
        if (condition.test(counter.entity)) {
            switch (method) {
                case MULTIPLY_BASE -> counter.addAttributeMultiplier(attribute, factor);
                case ADD -> counter.addAttributeAdder(attribute, factor);
            }
        }
    }

    @Override
    public Codec<? extends AbilityTree.NodeEffect> getCodec() {
        return CODEC;
    }
}
