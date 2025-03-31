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
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AccessoryEntities extends SimplePreparableReloadListener<Multimap<EntityType<?>, AccessorySlotType>> {
    public static final AccessoryEntities INSTANCE = new AccessoryEntities();
    private final Multimap<EntityType<?>, AccessorySlotType> validEntities = HashMultimap.create();

    public static EntityType<?> getApparentEntityType(LivingEntity entity) {
        return IAbstractChangedEntity.forEitherSafe(entity)
                .map(IAbstractChangedEntity::getChangedEntity)
                .map(ChangedEntity::getType)
                .orElse((EntityType)entity.getType());
    }

    public void forceReloadAccessories(LivingEntity entity) {
        AccessorySlots.getForEntity(entity).ifPresent(slots -> {
            if (!slots.initialize(
                    canEntityTypeUseSlot(getApparentEntityType(entity)),
                    AccessorySlots.defaultInvalidHandler(entity)))
                return;

            if (!entity.level.isClientSide)
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new AccessorySyncPacket(entity.getId(), slots));
        });
    }

    public Predicate<AccessorySlotType> canEntityTypeUseSlot(EntityType<?> entityType) {
        final var allowedSlots = validEntities.get(entityType);
        return allowedSlots::contains;
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

    public static class SyncPacket implements ChangedPacket {
        private final Multimap<EntityType<?>, AccessorySlotType> map;

        protected SyncPacket(Multimap<EntityType<?>, AccessorySlotType> map) {
            this.map = map;
        }

        public SyncPacket(FriendlyByteBuf buffer) {
            this.map = HashMultimap.create();
            buffer.readMap(FriendlyByteBuf::readInt, mapBuffer -> {
                return mapBuffer.readCollection(HashSet::new, FriendlyByteBuf::readInt);
            }).forEach((key, slots) -> {
                final EntityType<?> entityType = Registry.ENTITY_TYPE.byId(key);
                for (var slotTypeId : slots) {
                    map.put(entityType, ChangedRegistry.ACCESSORY_SLOTS.get().getValue(slotTypeId));
                }
            });
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            var intMap = new HashMap<Integer, Set<Integer>>();
            this.map.forEach((entityType, slotType) -> {
                intMap.computeIfAbsent(Registry.ENTITY_TYPE.getId(entityType), id -> new HashSet<>())
                        .add(ChangedRegistry.ACCESSORY_SLOTS.get().getID(slotType));
            });
            buffer.writeMap(intMap, FriendlyByteBuf::writeInt,
                    (setBuffer, intSet) -> setBuffer.writeCollection(intSet, FriendlyByteBuf::writeInt));
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            var context = contextSupplier.get();

            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                AccessoryEntities.INSTANCE.validEntities.clear();
                AccessoryEntities.INSTANCE.validEntities.putAll(this.map);

                context.setPacketHandled(true);
            }
        }
    }

    public SyncPacket syncPacket() {
        return new SyncPacket(this.validEntities);
    }
}
