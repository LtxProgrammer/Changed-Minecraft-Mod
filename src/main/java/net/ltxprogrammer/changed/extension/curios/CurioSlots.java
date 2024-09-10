package net.ltxprogrammer.changed.extension.curios;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CurioSlots extends SimplePreparableReloadListener<List<CurioSlots.SlotDefinition>> {
    public record SlotDefinition(String modId, String id, Integer priority, int size, boolean visible, boolean cosmetic, ResourceLocation icon) {
        public static class Builder {
            private final String identifier;
            private Integer priority;
            private int size = 1;
            private boolean visible = true;
            private boolean cosmetic = false;
            private ResourceLocation icon = null;

            public Builder(String identifier) {
                this.identifier = identifier;
            }

            public Builder icon(ResourceLocation icon) {
                this.icon = icon;
                return this;
            }

            public Builder priority(int priority) {
                this.priority = priority;
                return this;
            }

            public Builder size(int size) {
                this.size = size;
                return this;
            }

            public Builder hide() {
                this.visible = false;
                return this;
            }

            public Builder cosmetic() {
                this.cosmetic = true;
                return this;
            }

            public SlotDefinition build(String modId) {
                return new SlotDefinition(modId, identifier, priority, size, visible, cosmetic, icon);
            }
        }
    }

    public static final CurioSlots INSTANCE = new CurioSlots();
    private final List<SlotDefinition> slotDefinitions = new ArrayList<>();

    public boolean isDataSpecifiedSlot(String slot) {
        return slotDefinitions.stream().anyMatch(slotDefinition -> slotDefinition.id.equals(slot));
    }

    public void getSlotDefinitions(BiConsumer<String, SlotDefinition> consumer) {
        slotDefinitions.forEach(slotDefinition -> consumer.accept(slotDefinition.id, slotDefinition));
    }

    private SlotDefinition processJSONFile(String modId, String identifier, JsonObject root) {
        SlotDefinition.Builder builder = new SlotDefinition.Builder(identifier);

        if (root.has("size")) builder.size(root.get("size").getAsInt());
        if (root.has("order")) builder.priority(root.get("order").getAsInt());
        if (root.has("icon")) builder.icon(new ResourceLocation(root.get("icon").getAsString()));
        if (root.has("add_cosmetic") && root.get("add_cosmetic").getAsBoolean()) builder.cosmetic();

        return builder.build(modId);
    }

    @Override
    @NotNull
    protected List<SlotDefinition> prepare(@NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        final var entries = resources.listResources("curios/slots", filename ->
                ResourceLocation.isValidResourceLocation(filename) &&
                        filename.endsWith(".json"));

        List<SlotDefinition> working = new ArrayList<>();

        entries.forEach(filename -> {
            try {
                final String id = Path.of(filename.getPath()).getFileName().toString().replace(".json", "");
                final Resource content = resources.getResource(filename);

                try {
                    final Reader reader = new InputStreamReader(content.getInputStream(), StandardCharsets.UTF_8);

                    working.add(processJSONFile(filename.getNamespace(), id, JsonParser.parseReader(reader).getAsJsonObject()));

                    reader.close();
                } catch (Exception e) {
                    content.close();
                    throw e;
                }

                content.close();
            } catch (Exception e) {
                Changed.LOGGER.error("Failed to load entities for Curios from \"{}\"", filename);
            }
        });

        return working;
    }

    @Override
    protected void apply(@NotNull List<SlotDefinition> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        slotDefinitions.clear();
        slotDefinitions.addAll(output);
    }
}
