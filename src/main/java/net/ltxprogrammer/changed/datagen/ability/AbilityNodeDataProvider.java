package net.ltxprogrammer.changed.datagen.ability;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.ability.tree.*;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
        private final List<ResourceLocation> occludes = new ArrayList<>();
        private String titleId = "";
        private String requirementsId = "";
        private String descriptionId = "";
        private String flavorId = "";
        private int price = 0;
        private int groupDiscount = 0;
        private final List<NodeEffect> acquiredEffects = new ArrayList<>();
        private final List<NodeEffect> missingEffects = new ArrayList<>();

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
                    price, groupDiscount, acquiredEffects, missingEffects
            );
            node.setNodeLocation(loc);
            return node;
        }
    }
}
