package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.animations.AnimationDefinition;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ChangedFusions extends SimplePreparableReloadListener<List<ChangedFusions.FusionDefinition>> {
    public record FusionDefinition(ResourceLocation name, TransfurVariant<?> fusion, TransfurVariant<?> variant, Either<TransfurVariant<?>, Class<? extends LivingEntity>> other) {
        public static class Builder {
            private TransfurVariant<?> fusion = null;
            private TransfurVariant<?> variant = null;
            private TransfurVariant<?> otherVariant = null;
            private Class<? extends LivingEntity> mob = null;

            public Builder withFusion(ResourceLocation id) {
                this.fusion = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                return this;
            }

            public Builder withVariant(ResourceLocation id) {
                if (variant == null)
                    this.variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                else
                    this.otherVariant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                return this;
            }

            public Builder withMob(String className) throws ClassNotFoundException {
                this.mob = (Class<? extends LivingEntity>) Class.forName(className);
                return this;
            }

            public FusionDefinition build(ResourceLocation name) {
                Objects.requireNonNull(fusion);
                Objects.requireNonNull(variant);
                if (otherVariant == null && mob == null)
                    throw new NullPointerException();

                return new FusionDefinition(name, fusion, variant, otherVariant != null ? Either.left(otherVariant) : Either.right(mob));
            }
        }

        public boolean matches(TransfurVariant<?> variantA, TransfurVariant<?> variantB) {
            return other.left().filter(transfurVariant -> (variantA == variant && variantB == transfurVariant) ||
                    (variantA == transfurVariant && variantB == variant)).isPresent();
        }

        public boolean matches(TransfurVariant<?> variant, Class<? extends LivingEntity> mob) {
            return other.right().filter(mobClass -> variant == this.variant && mob == mobClass).isPresent();
        }
    }

    public static final ChangedFusions INSTANCE = new ChangedFusions();

    private final List<FusionDefinition> fusionDefinitions = new ArrayList<>();

    public Stream<FusionDefinition> getFusionDefinitions() {
        return fusionDefinitions.stream();
    }

    public Stream<TransfurVariant<?>> getFusionsFor(TransfurVariant<?> variantA, TransfurVariant<?> variantB) {
        return getFusionDefinitions().mapMulti((fusionDefinition, next) -> {
            if (fusionDefinition.matches(variantA, variantB))
                next.accept(fusionDefinition.fusion());
        });
    }

    public Stream<TransfurVariant<?>> getFusionsFor(TransfurVariant<?> variant, Class<? extends LivingEntity> mob) {
        return getFusionDefinitions().mapMulti((fusionDefinition, next) -> {
            if (fusionDefinition.matches(variant, mob))
                next.accept(fusionDefinition.fusion());
        });
    }

    private FusionDefinition processJSONFile(ResourceLocation name, JsonObject root) throws ClassNotFoundException {
        FusionDefinition.Builder builder = new FusionDefinition.Builder();

        if (root.has("fusion")) builder.withFusion(new ResourceLocation(root.get("fusion").getAsString()));
        if (root.has("variant")) builder.withVariant(new ResourceLocation(root.get("variant").getAsString()));
        if (root.has("otherVariant")) builder.withVariant(new ResourceLocation(root.get("otherVariant").getAsString()));
        if (root.has("mob")) builder.withMob(root.get("mob").getAsString());

        return builder.build(name);
    }

    @Override
    @NotNull
    protected List<FusionDefinition> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(new ArrayList<>(),
                resourceManager, "latex_fusions",
                (list, filename, id, json) -> list.add(processJSONFile(id, json)),
                (exception, filename) -> Changed.LOGGER.error("Failed to load latex fusions from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull List<FusionDefinition> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        fusionDefinitions.clear();
        fusionDefinitions.addAll(output);
    }
}
