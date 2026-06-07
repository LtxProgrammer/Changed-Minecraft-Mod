package net.ltxprogrammer.changed.datagen.ability;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.events.AbstractPointEvent;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
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

public abstract class AbilityTreeDataProvider implements DataProvider {
    protected final PackOutput output;
    protected final String modid;
    private final Map<ResourceLocation, AbilityTreeBuilder> treeBuilders = new HashMap<>();

    public AbilityTreeDataProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void addTrees();

    protected AbilityTreeBuilder addTree(ResourceLocation loc, List<RegistryElementPredicate<TransfurVariant<?>>> variants){
        return treeBuilders.computeIfAbsent(loc, l -> new AbilityTreeBuilder(variants));
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addTrees();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        Path outFolder = output.getOutputFolder(), path;
        AbilityTree tree;
        JsonElement json;
        for (var entry : treeBuilders.entrySet()) {
            ResourceLocation loc = entry.getKey();
            tree = entry.getValue().build(loc);

            path = outFolder.resolve("data/" + modid + "/ability/trees/" + loc.getPath() + ".json");

            json = AbilityTree.CODEC.encodeStart(JsonOps.INSTANCE, tree)
                    .result()
                    .orElseThrow(() -> new IllegalStateException("Failed to encode AbilityTree: " + loc));

            futures.add(DataProvider.saveStable(cache, json, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Ability Tree Provider";
    }

    public static final class AbilityTreeBuilder {
        private final List<RegistryElementPredicate<TransfurVariant<?>>> variants;
        private final List<AbstractPointEvent<?>> pointEvents = new ArrayList<>();
        private String titleId = "";
        private String flavorId = "";

        private AbilityTreeBuilder(List<RegistryElementPredicate<TransfurVariant<?>>> variants){
            this.variants = variants;
        }

        public AbilityTreeBuilder pointEvent(AbstractPointEvent<?> event) {
            this.pointEvents.add(event);
            return this;
        }

        public AbilityTreeBuilder pointEvents(List<AbstractPointEvent<?>> events) {
            this.pointEvents.addAll(events);
            return this;
        }

        public AbilityTreeBuilder title(String titleId) {
            this.titleId = titleId;
            return this;
        }

        public AbilityTreeBuilder flavor(String flavorId) {
            this.flavorId = flavorId;
            return this;
        }

        public AbilityTree build(ResourceLocation loc) {
            if (this.titleId.isEmpty()) {
                // Fallback automático ou padrão para facilitar a criação de chaves de tradução
                this.titleId = "ability.tree." + loc.getNamespace() + "." + loc.getPath();
            }
            AbilityTree tree = new AbilityTree(variants, pointEvents, titleId, flavorId);
            tree.setTreeLocation(loc);
            return tree;
        }
    }
}
