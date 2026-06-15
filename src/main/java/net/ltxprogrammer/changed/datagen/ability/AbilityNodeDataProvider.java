package net.ltxprogrammer.changed.datagen.ability;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.tree.*;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbilityNodeDataProvider implements DataProvider {
    protected final PackOutput output;
    protected final String modid;
    private final Map<ResourceLocation, AbilityNodeBuilder> nodeBuilders = new HashMap<>();

    public AbilityNodeDataProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void addNodes();

    protected AbilityNodeBuilder addNode(ResourceLocation loc){
        return nodeBuilders.computeIfAbsent(loc, l -> new AbilityNodeBuilder());
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
        private Either<ResourceLocation, TreeReference> parent;
        private NodeDisplayInfo displayInfo = NodeDisplayInfo.MISSING;
        private final List<ResourceLocation> occludes = new ObjectArrayList<>();
        private String titleId = "";
        private String requirementsId = "";
        private String descriptionId = "";
        private String flavorId = "";
        private int price;
        private int groupDiscount = 0;
        private int experiencePrice = 0;
        private int groupDiscountExperience = 0;
        private final List<NodePrice.ItemEntry> itemPrices = new ObjectArrayList<>();
        private final List<NodeEffect> acquiredEffects = new ObjectArrayList<>();
        private final List<NodeEffect> missingEffects = new ObjectArrayList<>();

        private AbilityNodeBuilder() {}

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

        public AbilityNodeBuilder price(int price) {
            this.price = price;
            return this;
        }

        public AbilityNodeBuilder groupDiscount(int groupDiscount) {
            this.groupDiscount = groupDiscount;
            return this;
        }

        public AbilityNodeBuilder price(int price, int groupDiscount) {
            return price(price).groupDiscount(groupDiscount);
        }

        public AbilityNodeBuilder experiencePrice(int levels) {
            this.experiencePrice = levels;
            return this;
        }

        public AbilityNodeBuilder experiencePriceDiscount(int levels) {
            this.groupDiscountExperience = levels;
            return this;
        }

        public AbilityNodeBuilder experiencePrice(int levels, int groupDiscount) {
            return experiencePrice(levels).experiencePriceDiscount(groupDiscount);
        }

        public AbilityNodeBuilder addItemCost(RegistryElementPredicate<Item> item) {
            return addItemCost(item, false);
        }

        public AbilityNodeBuilder addItemCost(RegistryElementPredicate<Item> item, boolean groupDiscounted) {
            this.itemPrices.add(new NodePrice.ItemEntry(item, groupDiscounted));
            return this;
        }

        public AbilityNodeBuilder addItemCost(ResourceLocation item) {
            return addItemCost(item, false);
        }

        public AbilityNodeBuilder addItemCost(ResourceLocation item, boolean groupDiscounted) {
            this.itemPrices.add(new NodePrice.ItemEntry(RegistryElementPredicate.forID(ForgeRegistries.ITEMS, item), groupDiscounted));
            return this;
        }

        public AbilityNodeBuilder addItemCost(TagKey<Item> item) {
            return addItemCost(item, false);
        }

        public AbilityNodeBuilder addItemCost(TagKey<Item> item, boolean groupDiscounted) {
            this.itemPrices.add(new NodePrice.ItemEntry(RegistryElementPredicate.forTag(ForgeRegistries.ITEMS, item), groupDiscounted));
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

        private AbilityNode build(ResourceLocation loc) {
            if (this.parent == null) {
                throw new IllegalStateException("AbilityNode '" + loc + "' must have a defined parent!");
            }
            if (this.titleId.isEmpty()) {
                this.titleId = "ability.node." + loc.getNamespace() + "." + loc.getPath();
            }

            AbilityNode node = new AbilityNode(
                    parent, displayInfo, occludes,
                    titleId, requirementsId, descriptionId, flavorId,
                    new NodePrice(price, groupDiscount, experiencePrice, groupDiscountExperience, itemPrices), acquiredEffects, missingEffects
            );
            node.setNodeLocation(loc);
            return node;
        }
    }
}
