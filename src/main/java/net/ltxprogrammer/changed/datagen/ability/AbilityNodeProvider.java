package net.ltxprogrammer.changed.datagen.ability;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
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

public abstract class AbilityNodeProvider implements DataProvider {
    protected final PackOutput output;
    protected final String modid;
    private final Map<ResourceLocation, AbilityNodeBuilder> nodeBuilders = new HashMap<>();

    public AbilityNodeProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void addNodes();

    protected AbilityNodeBuilder addNode(ResourceLocation loc, List<RegistryElementPredicate<TransfurVariant<?>>> variants){
        return nodeBuilders.computeIfAbsent(loc, l -> new AbilityNodeBuilder(variants));
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
        private final List<RegistryElementPredicate<TransfurVariant<?>>> variants;

        private AbilityNodeBuilder(List<RegistryElementPredicate<TransfurVariant<?>>> variants){
            this.variants = variants;
        }

        private AbilityNode build(ResourceLocation loc) {
            /*AbilityNode tree = new AbilityNode(variants);
            tree.setNodeLocation(loc);
            return tree;*/
            return null; // TODO
        }
    }
}
