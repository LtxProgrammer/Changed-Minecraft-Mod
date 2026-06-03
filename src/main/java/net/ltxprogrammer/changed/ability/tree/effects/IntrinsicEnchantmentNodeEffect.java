package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public class IntrinsicEnchantmentNodeEffect extends NodeEffect {
    public static final Codec<IntrinsicEnchantmentNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            ForgeRegistries.ENCHANTMENTS.getCodec().fieldOf("enchantment").forGetter(node -> node.enchantment),
            Method.CODEC.fieldOf("method").orElse(Method.MINIMUM).forGetter(node -> node.method),
            Codec.INT.fieldOf("level").forGetter(node -> node.level)
    ).apply(builder, IntrinsicEnchantmentNodeEffect::new));

    public enum Method implements StringRepresentable {
        MINIMUM("minimum"),
        MAXIMUM("maximum"),
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
    public final Enchantment enchantment;
    public final Method method;
    public final int level;

    public IntrinsicEnchantmentNodeEffect(AbstractCondition condition, Enchantment enchantment, Method method, int level) {
        this.condition = condition;
        this.enchantment = enchantment;
        this.method = method;
        this.level = level;
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
        return new IntrinsicEnchantmentNodeEffect(TrueCondition.INSTANCE, this.enchantment, this.method, this.level);
    }

    protected Component getFullEnchantmentName() {
        MutableComponent mutablecomponent = Component.translatable(enchantment.getDescriptionId());

        if (level != 1 || enchantment.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }

        return mutablecomponent;
    }

    @Override
    public void buildDescription(Consumer<Component> componentConsumer, boolean negate) {
        super.buildDescription(componentConsumer, negate);

        if (!negate)
            componentConsumer.accept(Component.translatable("text.changed.ability_tree.node.intrinsic_enchantment.plus",
                    getFullEnchantmentName()).withStyle(enchantment.isCurse() ? ChatFormatting.RED : ChatFormatting.BLUE));
        else
            componentConsumer.accept(Component.translatable("text.changed.ability_tree.node.intrinsic_enchantment.take",
                    getFullEnchantmentName()).withStyle(enchantment.isCurse() ? ChatFormatting.BLUE : ChatFormatting.RED));
    }
}
