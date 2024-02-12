package net.ltxprogrammer.changed.world;

import kroppeb.stareval.Util;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ChangedDataFixer {
    private static final HashMap<ResourceLocation, ResourceLocation> ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("dark_latex_wolf_male"), Changed.modResource("black_goo_wolf_male"));
        map.put(Changed.modResource("dark_latex_wolf_male_spawn_egg"), Changed.modResource("black_goo_wolf_male_spawn_egg"));
        map.put(Changed.modResource("form_dark_latex_wolf/male"), Changed.modResource("form_black_goo_wolf/male"));
        map.put(Changed.modResource("dark_latex_wolf_female"), Changed.modResource("black_goo_wolf_female"));
        map.put(Changed.modResource("dark_latex_wolf_female_spawn_egg"), Changed.modResource("black_goo_wolf_female_spawn_egg"));
        map.put(Changed.modResource("form_dark_latex_wolf/female"), Changed.modResource("form_black_goo_wolf/female"));
    });

    private static final HashMap<String, String> TAG_REMAP = Util.make(new HashMap<>(), map -> {
        map.put("LatexVariant", "TransfurVariant");
        map.put("LatexVariantAge", "TransfurVariantAge");
        map.put("LatexAbilities", "TransfurAbilities");
        map.put("LatexData", "TransfurData");
    });

    private static ResourceLocation updateID(ResourceLocation id) {
        return ID_REMAP.getOrDefault(id, id);
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

    private static void updateID(CompoundTag tag, String idName) {
        if (!tag.contains(idName)) return;
        final ResourceLocation id = ResourceLocation.tryParse(tag.getString(idName));
        if (id != null)
            tag.putString(idName, updateID(id).toString());
    }

    private static void updateEntity(CompoundTag entityTag) {
        updateID(entityTag, "id");
    }

    private static void updateItemTag(CompoundTag itemTag) {
        updateID(itemTag, "form");
    }

    private static void updateItem(CompoundTag itemStack) {
        updateID(itemStack, "id");
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

            updateID(tag, "TransfurVariant");
        });
    });

    private static final Consumer<CompoundTag> NULL_OP = tag -> {};

    public static void updateCompoundTag(DataFixTypes type, CompoundTag tag) {
        DATA_FIXERS.getOrDefault(type, NULL_OP).accept(tag);
    }
}
