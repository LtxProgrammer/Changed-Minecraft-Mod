package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class AbilityNode extends PartialNode {
    public static final Codec<AbilityNode> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.either(ResourceLocation.CODEC, TreeReference.CODEC).fieldOf("parent").forGetter(node -> node.parent),
            Codec.list(ResourceLocation.CODEC).fieldOf("occludes").orElseGet(List::of).forGetter(node -> node.occludes),
            Codec.STRING.fieldOf("titleId").forGetter(node -> node.titleId),
            Codec.STRING.fieldOf("requirementsId").orElse("").forGetter(node -> node.requirementsId),
            Codec.STRING.fieldOf("flavorId").forGetter(node -> node.flavorId),
            Codec.INT.fieldOf("price").forGetter(node -> node.price),
            Codec.INT.fieldOf("groupDiscount").orElse(0).forGetter(node -> node.groupDiscount),
            Codec.list(NodeEffect.EFFECT_CODEC).fieldOf("acquiredEffects").orElse(List.of()).forGetter(node -> node.acquiredEffects),
            Codec.list(NodeEffect.EFFECT_CODEC).fieldOf("missingEffects").orElse(List.of()).forGetter(node -> node.missingEffects)
    ).apply(builder, AbilityNode::new));

    public final List<ResourceLocation> occludes;
    public final List<NodeEffect> acquiredEffects;
    public final List<NodeEffect> missingEffects;

    /*public final Map<String, Criterion> criteria;
    public final String[][] requirements;*/

    public AbilityNode(Either<ResourceLocation, TreeReference> parent, List<ResourceLocation> occludes,
                       String titleId, String requirementsId, String flavorId,
                       int price, int groupDiscount,
                       List<NodeEffect> acquiredEffects, List<NodeEffect> missingEffects) {
        super(parent, titleId, requirementsId, flavorId, price, groupDiscount);

        this.occludes = occludes;
        this.acquiredEffects = acquiredEffects;
        this.missingEffects = missingEffects;
    }

    /*public Advancement.Builder createAdvancement() {
        Advancement.Builder builder = Advancement.Builder.advancement();
        criteria.forEach(builder::addCriterion);
        builder.requirements(requirements);
        builder.display(new DisplayInfo(
                ItemStack.EMPTY,
                Component.translatable(titleId),
                Component.translatable(titleId),
                *//* background *//* null,
                FrameType.GOAL,
                *//* showToast *//* true, *//* announceChat *//* false, *//* hidden *//* true));
        return builder;
    }*/
}
