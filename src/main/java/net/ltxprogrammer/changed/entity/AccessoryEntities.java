package net.ltxprogrammer.changed.entity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.AccessorySyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class AccessoryEntities extends SimplePreparableReloadListener<Multimap<EntityType<?>, AccessorySlotType>> {
    public static final AccessoryEntities INSTANCE = new AccessoryEntities();
    private final Multimap<EntityType<?>, AccessorySlotType> validEntities = HashMultimap.create();

    private EntityType<?> getApparentEntityType(LivingEntity entity) {
        return IAbstractChangedEntity.forEitherSafe(entity)
                .map(IAbstractChangedEntity::getChangedEntity)
                .map(ChangedEntity::getType)
                .orElse((EntityType)entity.getType());
    }

    public void forceReloadAccessories(LivingEntity entity) {
        AccessorySlots.getForEntity(entity).ifPresent(slots -> {
            slots.initialize(
                    slotType -> canEntityTypeUseSlot(getApparentEntityType(entity), slotType),
                    AccessorySlots.defaultInvalidHandler(entity));

            if (!entity.level.isClientSide)
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new AccessorySyncPacket(entity.getId(), slots));
        });
    }

    public boolean canEntityTypeUseSlot(EntityType<?> entityType, AccessorySlotType slot) {
         return validEntities.get(entityType).contains(slot);
    }

    private Multimap<EntityType<?>, AccessorySlotType> processJSONFile(JsonObject root) {
        Multimap<EntityType<?>, AccessorySlotType> working = HashMultimap.create();

        root.getAsJsonArray("entities").forEach(entity -> {
            final ResourceLocation entityId = new ResourceLocation(entity.getAsString());
            if (!ForgeRegistries.ENTITIES.containsKey(entityId))
                throw new IllegalArgumentException("Unknown entity " + entityId);
            final EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entity.getAsString()));

            root.getAsJsonArray("slots").forEach(slot -> {
                working.put(entityType, ChangedRegistry.ACCESSORY_SLOTS.get().getValue(new ResourceLocation(slot.getAsString())));
            });
        });

        return working;
    }

    @Override
    @NotNull
    public Multimap<EntityType<?>, AccessorySlotType> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        final var entries = resources.listResources("accessories/entities", filename ->
                ResourceLocation.isValidResourceLocation(filename) &&
                        filename.endsWith(".json"));

        Multimap<EntityType<?>, AccessorySlotType> working = HashMultimap.create();

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
    protected void apply(@NotNull Multimap<EntityType<?>, AccessorySlotType> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        validEntities.clear();
        validEntities.putAll(output);
    }
}
