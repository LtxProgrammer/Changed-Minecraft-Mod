package net.ltxprogrammer.changed.datagen.ability;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.NodeDisplayInfo;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.NodePrice;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.ability.tree.requirements.AbstractRequirement;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbilityNodeDataProvider implements DataProvider {
    protected final PackOutput output;
    protected final String modid;
    protected final Map<ResourceLocation, AbilityNodeBuilder> nodeBuilders = new HashMap<>();

    public AbilityNodeDataProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void addNodes();

    protected AbilityNodeBuilder addNode(ResourceLocation loc) {
        return nodeBuilders.computeIfAbsent(loc, l -> new AbilityNodeBuilder());
    }

    protected AbilityNodeBuilder addNode(AbilityNode node) {
        return nodeBuilders.computeIfAbsent(node.getNodeLocation(), l -> AbilityNodeBuilder.fromAbilityNode(node));
    }

    protected AbilityNodeBuilder addNode(ResourceLocation nodeLoc, AbilityNodeBuilder abilityNodeBuilder) {
        return nodeBuilders.computeIfAbsent(nodeLoc, l -> abilityNodeBuilder);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addNodes();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        Path outFolder = output.getOutputFolder(), path;
        AbilityNode node;
        JsonElement json;
        for (var entry : nodeBuilders.entrySet()) {
            ResourceLocation loc = entry.getKey();
            node = entry.getValue().build(loc);

            path = outFolder.resolve("data/" + modid + "/ability/nodes/" + loc.getPath() + ".json");

            json = AbilityNode.CODEC.encodeStart(JsonOps.INSTANCE, node)
                    .result()
                    .orElseThrow(() -> new IllegalStateException("Failed to encode AbilityNode: " + loc));

            futures.add(DataProvider.saveStable(cache, json, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Ability Node Provider";
    }

    public static final class AbilityNodeBuilder {
        @Nullable
        private ResourceLocation nodeLocation = null; //Is Here as a fail safe.
        private Either<ResourceLocation, TreeReference> parent;
        private NodeDisplayInfo displayInfo = NodeDisplayInfo.MISSING;
        private List<AbstractRequirement> requirements = new ObjectArrayList<>();
        private List<ResourceLocation> occludes = new ObjectArrayList<>();
        private String titleId = "";
        private String requirementsId = "";
        private String descriptionId = "";
        private String flavorId = "";
        private NodePrice nodePrice = new NodePrice(
                0,
                0,
                0,
                0,
                new ArrayList<>(),
                null
        );
        private List<NodeEffect> acquiredEffects = new ObjectArrayList<>();
        private List<NodeEffect> missingEffects = new ObjectArrayList<>();

        private AbilityNodeBuilder() {
        }

        public static AbilityNodeBuilder fromAbilityNode(AbilityNode node) {
            AbilityNodeBuilder builder = builder();
            builder.parent = node.parent;
            builder.displayInfo = node.displayInfo;
            builder.requirements = node.requirements;
            builder.occludes = node.occludes;
            builder.titleId = node.titleId;
            builder.requirementsId = node.requirementsId;
            builder.descriptionId = node.descriptionId;
            builder.nodePrice = node.price;
            builder.acquiredEffects = node.acquiredEffects;
            builder.missingEffects = node.missingEffects;
            return builder;
        }

        public static AbilityNodeBuilder builder() {
            return new AbilityNodeBuilder();
        }

        public AbilityNodeBuilder parent(ResourceLocation parentNode) {
            this.parent = Either.left(parentNode);
            return this;
        }

        public AbilityNodeBuilder parent(TreeReference treeRootReference) {
            this.parent = Either.right(treeRootReference);
            return this;
        }

        public AbilityNodeBuilder display(NodeDisplayInfo displayInfo) {
            this.displayInfo = displayInfo;
            return this;
        }

        public AbilityNodeBuilder occludes(ResourceLocation nodeLocation) {
            this.occludes.add(nodeLocation);
            return this;
        }

        public AbilityNodeBuilder title(String titleId) {
            this.titleId = titleId;
            return this;
        }

        public AbilityNodeBuilder requirements(String requirementsId) {
            this.requirementsId = requirementsId;
            return this;
        }

        public AbilityNodeBuilder description(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public AbilityNodeBuilder flavor(String flavorId) {
            this.flavorId = flavorId;
            return this;
        }

        public AbilityNodeBuilder level(int level) {
            this.nodePrice = new NodePrice(level,
                    nodePrice.groupDiscountLevels(),
                    nodePrice.experience(),
                    nodePrice.groupDiscountExperience(),
                    nodePrice.items(),
                    nodePrice.computedDiscountPrice()
            );
            return this;
        }

        public AbilityNodeBuilder groupDiscount(int groupDiscount) {
            this.nodePrice = new NodePrice(nodePrice.levels(),
                    groupDiscount,
                    nodePrice.experience(),
                    nodePrice.groupDiscountExperience(),
                    nodePrice.items(),
                    nodePrice.computedDiscountPrice());
            return this;
        }

        public AbilityNodeBuilder level(int price, int groupDiscount) {
            return level(price).groupDiscount(groupDiscount);
        }

        public AbilityNodeBuilder experiencePrice(int levels) {
            this.nodePrice = new NodePrice(nodePrice.levels(),
                    nodePrice.groupDiscountLevels(),
                    nodePrice.experience(),
                    nodePrice.groupDiscountExperience(),
                    nodePrice.items(),
                    nodePrice.computedDiscountPrice());
            return this;
        }

        public AbilityNodeBuilder experiencePriceDiscount(int levels) {
            this.nodePrice = new NodePrice(nodePrice.levels(),
                    nodePrice.groupDiscountLevels(),
                    nodePrice.experience(),
                    levels,
                    nodePrice.items(),
                    nodePrice.computedDiscountPrice());
            return this;
        }

        public AbilityNodeBuilder experiencePrice(int levels, int groupDiscount) {
            return experiencePrice(levels).experiencePriceDiscount(groupDiscount);
        }

        public AbilityNodeBuilder addItemCost(RegistryElementPredicate<Item> item) {
            return addItemCost(item, false);
        }

        public AbilityNodeBuilder addItemCost(RegistryElementPredicate<Item> item, boolean groupDiscounted) {
            List<NodePrice.ItemEntry> itemEntries = new ArrayList<>(nodePrice.items());
            itemEntries.add(new NodePrice.ItemEntry(item, groupDiscounted));
            this.nodePrice = new NodePrice(nodePrice.levels(),
                    nodePrice.groupDiscountLevels(),
                    nodePrice.experience(),
                    nodePrice.levels(),
                    itemEntries,
                    nodePrice.computedDiscountPrice());
            return this;
        }

        public AbilityNodeBuilder addItemCost(ResourceLocation item) {
            return addItemCost(item, false);
        }

        public AbilityNodeBuilder addItemCost(ResourceLocation item, boolean groupDiscounted) {
            List<NodePrice.ItemEntry> itemPrices = new ArrayList<>(nodePrice.items());
            itemPrices.add(new NodePrice.ItemEntry(RegistryElementPredicate.forID(ForgeRegistries.ITEMS, item), groupDiscounted));
            this.nodePrice = new NodePrice(nodePrice.levels(),
                    nodePrice.groupDiscountLevels(),
                    nodePrice.experience(),
                    nodePrice.levels(),
                    itemPrices,
                    nodePrice.computedDiscountPrice());
            return this;
        }

        public AbilityNodeBuilder addItemCost(TagKey<Item> item) {
            return addItemCost(item, false);
        }

        public AbilityNodeBuilder addItemCost(TagKey<Item> item, boolean groupDiscounted) {
            List<NodePrice.ItemEntry> itemPrices = new ArrayList<>(nodePrice.items());
            itemPrices.add(new NodePrice.ItemEntry(RegistryElementPredicate.forTag(ForgeRegistries.ITEMS, item), groupDiscounted));
            this.nodePrice = new NodePrice(nodePrice.levels(),
                    nodePrice.groupDiscountLevels(),
                    nodePrice.experience(),
                    nodePrice.levels(),
                    itemPrices,
                    nodePrice.computedDiscountPrice());
            return this;
        }

        public AbilityNodeBuilder acquiredEffect(NodeEffect effect) {
            this.acquiredEffects.add(effect);
            return this;
        }

        public AbilityNodeBuilder missingEffect(NodeEffect effect) {
            this.missingEffects.add(effect);
            return this;
        }

        public AbilityNodeBuilder addRequirement(AbstractRequirement requirement) {
            this.requirements.add(requirement);
            return this;
        }

        public AbilityNodeBuilder withNodeLocation(ResourceLocation nodeLocation) {
            this.nodeLocation = nodeLocation;
            return this;
        }

        public AbilityNode build(ResourceLocation loc) {
            if (nodeLocation != null && loc == null) loc = nodeLocation;
            if (this.parent == null) {
                throw new IllegalStateException("AbilityNode '" + loc + "' must have a defined parent!");
            }
            if (this.titleId.isEmpty()) {
                this.titleId = "ability.node." + loc.getNamespace() + "." + loc.getPath();
            }

            AbilityNode node = new AbilityNode(
                    parent, displayInfo, requirements, occludes,
                    titleId, requirementsId, descriptionId, flavorId,
                    nodePrice, acquiredEffects, missingEffects
            );
            node.setNodeLocation(loc);
            return node;
        }
    }
}
