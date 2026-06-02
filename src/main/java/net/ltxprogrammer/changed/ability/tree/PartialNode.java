package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class PartialNode {
    public record TreeReference(ResourceLocation treeName) {
            public static final Codec<TreeReference> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ResourceLocation.CODEC.fieldOf("tree").forGetter(node -> node.treeName)
            ).apply(builder, TreeReference::new));
    }

    public static final Codec<PartialNode> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.either(ResourceLocation.CODEC, TreeReference.CODEC).fieldOf("parent").forGetter(node -> node.parent),
            Codec.STRING.fieldOf("titleId").forGetter(node -> node.titleId),
            Codec.STRING.fieldOf("requirementsId").orElse("").forGetter(node -> node.requirementsId),
            Codec.STRING.fieldOf("descriptionId").orElse("").forGetter(node -> node.descriptionId),
            Codec.STRING.fieldOf("flavorId").orElse("").forGetter(node -> node.flavorId),
            Codec.INT.fieldOf("price").forGetter(node -> node.price),
            Codec.INT.fieldOf("groupDiscount").orElse(0).forGetter(node -> node.groupDiscount)
    ).apply(builder, PartialNode::new));

    public final Either<ResourceLocation, TreeReference> parent;
    public final String titleId;
    public final String requirementsId;
    public final String descriptionId;
    public final String flavorId;
    public final int price;
    public final int groupDiscount;

    private ResourceLocation nodeLocation;

    public PartialNode(Either<ResourceLocation, TreeReference> parent, String titleId, String requirementsId, String descriptionId, String flavorId, int price, int groupDiscount) {
        this.parent = parent;
        this.titleId = titleId;
        this.requirementsId = requirementsId;
        this.descriptionId = descriptionId;
        this.flavorId = flavorId;
        this.price = price;
        this.groupDiscount = groupDiscount;
    }

    public void setNodeLocation(ResourceLocation nodeLocation) {
        this.nodeLocation = nodeLocation;
    }

    public ResourceLocation getNodeLocation() {
        return nodeLocation;
    }

    public Component getTitle() {
        return Component.translatable(titleId)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
    }

    public Optional<Component> getFlavorText() {
        if (flavorId.isEmpty())
            return Optional.empty();

        return Optional.of(Component.literal("\"")
                .append(Component.translatable(flavorId))
                .append(Component.literal("\""))
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.GRAY)
                        .withItalic(true)));
    }
}
