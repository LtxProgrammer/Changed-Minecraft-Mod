package net.ltxprogrammer.changed.extension.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CurioEntities extends SimplePreparableReloadListener<Multimap<EntityType<?>, String>> {
    public static final CurioEntities INSTANCE = new CurioEntities();
    private final Multimap<EntityType<?>, String> validEntities = HashMultimap.create();

    public void forceReloadCurios(LivingEntity entity) {
        CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(curios -> {
            final var inv = curios.writeTag();
            curios.reset();
            curios.readTag(inv);

            final var curiosMap = new HashMap<>(curios.getCurios());
            final var expectedSlots = CuriosApi.getSlotHelper().getSlotTypes(entity).stream().map(ISlotType::getIdentifier).collect(Collectors.toSet());
            final var invalidSlots = curiosMap.keySet().stream()
                    .filter(slot -> !expectedSlots.contains(slot))
                    .filter(CurioSlots.INSTANCE::isDataSpecifiedSlot)
                    .collect(Collectors.toSet());

            invalidSlots.forEach(slot -> {
                final var handler = curiosMap.get(slot);
                final var stacks = handler.getStacks();
                for (int i = 0; i < stacks.getSlots(); ++i) {
                    curios.loseInvalidStack(stacks.getStackInSlot(i));
                }

                curiosMap.remove(slot);
            });

            curios.setCurios(curiosMap);
        });
    }

    public boolean canEntityTypeUseSlot(EntityType<?> entityType, String slot) {
        if (!CurioSlots.INSTANCE.isDataSpecifiedSlot(slot))
            return true; // Applies unknown slots by default

         return validEntities.get(entityType).contains(slot);
    }

    private Multimap<EntityType<?>, String> processJSONFile(JsonObject root) {
        Multimap<EntityType<?>, String> working = HashMultimap.create();

        root.getAsJsonArray("entities").forEach(entity -> {
            final ResourceLocation entityId = new ResourceLocation(entity.getAsString());
            if (!ForgeRegistries.ENTITIES.containsKey(entityId))
                throw new IllegalArgumentException("Unknown entity " + entityId);
            final EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entity.getAsString()));

            root.getAsJsonArray("slots").forEach(slot -> {
                working.put(entityType, slot.getAsString());
            });
        });

        return working;
    }

    @Override
    @NotNull
    public Multimap<EntityType<?>, String> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        final var entries = resources.listResources("curios/entities", filename ->
                ResourceLocation.isValidResourceLocation(filename) &&
                        filename.endsWith(".json"));

        Multimap<EntityType<?>, String> working = HashMultimap.create();

        entries.forEach(filename -> {
            try {
                final Resource content = resources.getResource(filename);

                try {
                    final Reader reader = new InputStreamReader(content.getInputStream(), StandardCharsets.UTF_8);

                    working.putAll(processJSONFile(JsonParser.parseReader(reader).getAsJsonObject()));

                    reader.close();
                } catch (Exception e) {
                    content.close();
                    throw e;
                }

                content.close();
            } catch (Exception e) {
                Changed.LOGGER.error("Failed to load entities for Curios from \"{}\" : {}", filename, e);
            }
        });

        return working;
    }

    @Override
    protected void apply(@NotNull Multimap<EntityType<?>, String> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        validEntities.clear();
        validEntities.putAll(output);
    }
}
