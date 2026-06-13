package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PostChainNodeEffect extends NodeEffect {
    public static final Codec<PostChainNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            ResourceLocation.CODEC.fieldOf("postChain").forGetter(node -> node.postChain),
            Codec.INT.fieldOf("priority").orElse(0).forGetter(node -> node.priority),
            Codec.FLOAT.fieldOf("strength").orElse(1.0f).forGetter(node -> node.strength)
    ).apply(builder, PostChainNodeEffect::new));

    public final AbstractCondition condition;
    public final ResourceLocation postChain;
    public final int priority;
    public final float strength;

    public PostChainNodeEffect(AbstractCondition condition, ResourceLocation postChain, int priority, float strength) {
        this.condition = condition;
        this.postChain = postChain;
        this.priority = priority;
        this.strength = strength;
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
        return new PostChainNodeEffect(TrueCondition.INSTANCE, this.postChain, this.priority, this.strength);
    }
}
