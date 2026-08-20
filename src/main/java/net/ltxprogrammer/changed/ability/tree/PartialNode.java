package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.tree.requirements.RequirementProgress;
import net.ltxprogrammer.changed.data.codec.OptionalKeyFieldCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class PartialNode {
    public record TreeReference(ResourceLocation treeName) {
            public static final Codec<TreeReference> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ResourceLocation.CODEC.fieldOf("tree").forGetter(node -> node.treeName)
            ).apply(builder, TreeReference::new));

            public static TreeReference fromBuffer(FriendlyByteBuf buffer) {
                return new TreeReference(buffer.readResourceLocation());
            }

            public void writeToBuffer(FriendlyByteBuf buffer) {
                buffer.writeResourceLocation(treeName);
            }
    }

    public static final Codec<PartialNode> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.either(ResourceLocation.CODEC, TreeReference.CODEC).fieldOf("parent").forGetter(node -> node.parent),
            OptionalKeyFieldCodec.keyOptionalFieldOf("display", NodeDisplayInfo.CODEC, NodeDisplayInfo.MISSING).forGetter(node -> node.displayInfo),
            Codec.STRING.fieldOf("titleId").forGetter(node -> node.titleId),
            Codec.STRING.fieldOf("requirementsId").orElse("").forGetter(node -> node.requirementsId),
            Codec.STRING.fieldOf("descriptionId").orElse("").forGetter(node -> node.descriptionId),
            Codec.STRING.fieldOf("flavorId").orElse("").forGetter(node -> node.flavorId),
            NodePrice.CODEC.fieldOf("price").forGetter(node -> node.price)
    ).apply(builder, PartialNode::new));

    public final Either<ResourceLocation, TreeReference> parent;
    public final NodeDisplayInfo displayInfo;
    public final String titleId;
    public final String requirementsId;
    public final String descriptionId;
    public final String flavorId;
    public final NodePrice price;

    private ResourceLocation nodeLocation;
    private final List<RequirementProgress<?>> requirementProgress = new ObjectArrayList<>();

    public PartialNode(Either<ResourceLocation, TreeReference> parent, NodeDisplayInfo displayInfo, String titleId, String requirementsId, String descriptionId, String flavorId, NodePrice price) {
        this.parent = parent;
        this.displayInfo = displayInfo;
        this.titleId = titleId;
        this.requirementsId = requirementsId;
        this.descriptionId = descriptionId;
        this.flavorId = flavorId;
        this.price = price;
    }

    public void setNodeLocation(ResourceLocation nodeLocation) {
        this.nodeLocation = nodeLocation;
    }

    public ResourceLocation getNodeLocation() {
        return nodeLocation;
    }

    public List<RequirementProgress<?>> getRequirementProgress() {
        return requirementProgress;
    }

    public MutableComponent getTitle() {
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
