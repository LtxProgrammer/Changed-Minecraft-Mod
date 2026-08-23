package net.ltxprogrammer.changed.datagen.animations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.entity.animation.AnimationCategory;
import net.ltxprogrammer.changed.entity.animation.AnimationEvent;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// @author Foxyas
public abstract class AnimationAssociationsDataProvider implements DataProvider {
    private final PackOutput packOutput;
    private final String modId;
    private final Map<ResourceLocation, JsonObject> entries = new HashMap<>();

    public AnimationAssociationsDataProvider(PackOutput packOutput, String modId) {
        this.packOutput = packOutput;
        this.modId = modId;
    }

    protected abstract void registerAssociations();

    protected EventBuilder add(AnimationEvent<?> event, AnimationCategory category) {
        return add(ChangedRegistry.ANIMATION_EVENTS.getKey(event), category);
    }

    protected EventBuilder add(ResourceLocation eventKey, AnimationCategory category) {
        JsonObject eventObj = entries.computeIfAbsent(eventKey, key -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("category", category.getSerializedName());
            obj.add("animations", new JsonArray());
            return obj;
        });
        return new EventBuilder(eventObj.getAsJsonArray("animations"));
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        entries.clear();
        registerAssociations();

        JsonObject root = new JsonObject();
        entries.forEach((eventKey, jsonObject) -> root.add(eventKey.toString(), jsonObject));

        Path path = this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(this.modId)
                .resolve("animations.json");

        return DataProvider.saveStable(output, root, path);
    }

    @Override
    public @NotNull String getName() {
        return "Animation Associations: " + modId;
    }

    // Builder for animations within a single event
    public static class EventBuilder {
        private final JsonArray animationsArray;

        public EventBuilder(JsonArray animationsArray) {
            this.animationsArray = animationsArray;
        }

        public EventBuilder addAnimation(ResourceLocation animationName, JsonObject criteria) {
            JsonObject animObj = new JsonObject();
            animObj.addProperty("name", animationName.toString());
            animObj.add("criteria", criteria);

            this.animationsArray.add(animObj);
            return this;
        }

        public EventBuilder addAnimation(ResourceLocation animationName, AnimationCriteriaBuilder criteriaBuilder) {
            return addAnimation(animationName, criteriaBuilder.build());
        }

        public EventBuilder addAnimation(ResourceLocation animationName) {
            return addAnimation(animationName, new JsonObject());
        }
    }
}
