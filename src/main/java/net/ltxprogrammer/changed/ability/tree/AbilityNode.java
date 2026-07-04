package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.requirements.AbstractRequirement;
import net.ltxprogrammer.changed.data.codec.OptionalKeyFieldCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class AbilityNode extends PartialNode {
    public static final Codec<AbilityNode> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.either(ResourceLocation.CODEC, TreeReference.CODEC).fieldOf("parent").forGetter(node -> node.parent),
            OptionalKeyFieldCodec.keyOptionalFieldOf("display", NodeDisplayInfo.CODEC, NodeDisplayInfo.MISSING).forGetter(node -> node.displayInfo),
            OptionalKeyFieldCodec.keyOptionalFieldOf("requirements", Codec.list(AbstractRequirement.REQUIREMENT_CODEC), List.of()).forGetter(node -> node.requirements),
            Codec.list(ResourceLocation.CODEC).fieldOf("occludes").orElseGet(List::of).forGetter(node -> node.occludes),
            Codec.STRING.fieldOf("titleId").forGetter(node -> node.titleId),
            Codec.STRING.fieldOf("requirementsId").orElse("").forGetter(node -> node.requirementsId),
            Codec.STRING.fieldOf("descriptionId").orElse("").forGetter(node -> node.descriptionId),
            Codec.STRING.fieldOf("flavorId").orElse("").forGetter(node -> node.flavorId),
            NodePrice.CODEC.fieldOf("price").forGetter(node -> node.price),
            OptionalKeyFieldCodec.keyOptionalFieldOf("acquiredEffects", Codec.list(NodeEffect.EFFECT_CODEC), List.of()).forGetter(node -> node.acquiredEffects),
            OptionalKeyFieldCodec.keyOptionalFieldOf("missingEffects", Codec.list(NodeEffect.EFFECT_CODEC), List.of()).forGetter(node -> node.missingEffects)
    ).apply(builder, AbilityNode::new));

    public final List<AbstractRequirement> requirements;
    public final List<ResourceLocation> occludes;
    public final List<NodeEffect> acquiredEffects;
    public final List<NodeEffect> missingEffects;

    public AbilityNode(Either<ResourceLocation, TreeReference> parent, NodeDisplayInfo displayInfo,
                       List<AbstractRequirement> requirements, List<ResourceLocation> occludes,
                       String titleId, String requirementsId, String descriptionId, String flavorId,
                       NodePrice price, List<NodeEffect> acquiredEffects, List<NodeEffect> missingEffects) {
        super(parent, displayInfo, titleId, requirementsId, descriptionId, flavorId, price);

        this.requirements = requirements;
        this.occludes = occludes;
        this.acquiredEffects = acquiredEffects;
        this.missingEffects = missingEffects;
    }

    public void buildDescription(Consumer<Component> componentConsumer) {
        if (!descriptionId.isEmpty()) {
            componentConsumer.accept(Component.translatable(descriptionId).withStyle(ChatFormatting.BLUE));
            return;
        }

        for (var effect : missingEffects) {
            effect.buildDescription(componentConsumer, true);
        }

        for (var effect : acquiredEffects) {
            effect.buildDescription(componentConsumer, false);
        }
    }
}
