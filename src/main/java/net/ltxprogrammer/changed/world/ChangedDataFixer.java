package net.ltxprogrammer.changed.world;

import kroppeb.stareval.Util;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChangedDataFixer {
    private static <T extends Entity> ResourceLocation spawnEggHelper(RegistryObject<EntityType<T>> entity) {
        return Objects.requireNonNull(ChangedEntities.SPAWN_EGGS.get(entity)).getId();
    }

    private static final HashMap<ResourceLocation, ResourceLocation> ENTITY_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("dark_latex_wolf_male"), ChangedEntities.BLACK_GOO_WOLF_MALE.getId());
        map.put(Changed.modResource("dark_latex_wolf_female"), ChangedEntities.BLACK_GOO_WOLF_FEMALE.getId());
        map.put(Changed.modResource("light_latex_wolf_male"), ChangedEntities.WHITE_GOO_WOLF_MALE.getId());
        map.put(Changed.modResource("light_latex_wolf_female"), ChangedEntities.WHITE_GOO_WOLF_FEMALE.getId());
        map.put(Changed.modResource("white_latex_wolf"), ChangedEntities.PURE_WHITE_GOO_WOLF.getId());
    });

    private static final HashMap<ResourceLocation, ResourceLocation> ITEM_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("dark_latex_wolf_male_spawn_egg"), spawnEggHelper(ChangedEntities.BLACK_GOO_WOLF_MALE));
        map.put(Changed.modResource("dark_latex_wolf_female_spawn_egg"), spawnEggHelper(ChangedEntities.BLACK_GOO_WOLF_FEMALE));
        map.put(Changed.modResource("light_latex_wolf_male_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_GOO_WOLF_MALE));
        map.put(Changed.modResource("light_latex_wolf_female_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_GOO_WOLF_FEMALE));
        map.put(Changed.modResource("light_latex_wolf_organic_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_WOLF));
        map.put(Changed.modResource("white_latex_wolf_spawn_egg"), spawnEggHelper(ChangedEntities.PURE_WHITE_GOO_WOLF));
    });

    private static final HashMap<ResourceLocation, ResourceLocation> VARIANT_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("form_dark_latex_wolf/male"), TransfurVariant.BLACK_GOO_WOLF.male().getFormId());
        map.put(Changed.modResource("form_dark_latex_wolf/female"), TransfurVariant.BLACK_GOO_WOLF.female().getFormId());
        map.put(Changed.modResource("form_light_latex_wolf/male"), TransfurVariant.WHITE_GOO_WOLF.male().getFormId());
        map.put(Changed.modResource("form_light_latex_wolf/female"), TransfurVariant.WHITE_GOO_WOLF.female().getFormId());
        map.put(Changed.modResource("form_light_latex_wolf_organic"), TransfurVariant.WHITE_WOLF.getFormId());
        map.put(Changed.modResource("form_white_latex_wolf"), TransfurVariant.PURE_WHITE_GOO_WOLF.getFormId());
    });

    private static final HashMap<String, String> TAG_REMAP = Util.make(new HashMap<>(), map -> {
        map.put("LatexVariant", "TransfurVariant");
        map.put("LatexVariantAge", "TransfurVariantAge");
        map.put("LatexAbilities", "TransfurAbilities");
        map.put("LatexData", "TransfurData");
    });

    private static ResourceLocation updateID(Map<ResourceLocation, ResourceLocation> remap, ResourceLocation id) {
        return remap.getOrDefault(id, id);
    }

    private static void updateTagNames(CompoundTag tag) {
        tag.getAllKeys().stream().toList().forEach(key -> {
            if (TAG_REMAP.containsKey(key)) {
                final var newKey = TAG_REMAP.get(key);
                final var subTag = tag.get(key);
                if (subTag != null && !newKey.equals(key)) {
                    tag.put(newKey, subTag);
                    tag.remove(key);
                }
            }
        });
    }

    private static void updateID(Map<ResourceLocation, ResourceLocation> remap, CompoundTag tag, String idName) {
        if (!tag.contains(idName)) return;
        final ResourceLocation id = ResourceLocation.tryParse(tag.getString(idName));
        if (id != null)
            tag.putString(idName, updateID(remap, id).toString());
    }

    private static void updateEntity(CompoundTag entityTag) {
        updateID(ENTITY_ID_REMAP, entityTag, "id");
    }

    private static void updateItemTag(CompoundTag itemTag) {
        updateID(VARIANT_ID_REMAP, itemTag, "form");
    }

    private static void updateItem(CompoundTag itemStack) {
        updateID(ITEM_ID_REMAP, itemStack, "id");
        if (itemStack.contains("tag")) {
            updateItemTag(itemStack.getCompound("tag"));
        }
    }

    private static final Map<DataFixTypes, Consumer<CompoundTag>> DATA_FIXERS = Util.make(new HashMap<>(), map -> {
        map.put(DataFixTypes.ENTITY_CHUNK, tag -> {
            if (tag.get("Entities") instanceof ListTag listTag) {
                listTag.forEach(entityTag -> {
                    if (entityTag instanceof CompoundTag compoundTag)
                        updateEntity(compoundTag);
                });
            }
        });
        map.put(DataFixTypes.PLAYER, tag -> {
            updateTagNames(tag);

            if (tag.get("Inventory") instanceof ListTag listTag) {
                listTag.forEach(entityTag -> {
                    if (entityTag instanceof CompoundTag compoundTag)
                        updateItem(compoundTag);
                });
            }

            updateID(VARIANT_ID_REMAP, tag, "TransfurVariant");
        });
    });

    private static final Consumer<CompoundTag> NULL_OP = tag -> {};

    public static void updateCompoundTag(DataFixTypes type, CompoundTag tag) {
        DATA_FIXERS.getOrDefault(type, NULL_OP).accept(tag);
    }
}
