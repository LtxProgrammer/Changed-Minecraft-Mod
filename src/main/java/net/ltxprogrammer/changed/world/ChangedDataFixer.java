package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ChangedDataFixer {
    public static final int DATAFIX_ID = 1;

    private static <T extends Entity> ResourceLocation spawnEggHelper(RegistryObject<EntityType<T>> entity) {
        return Objects.requireNonNull(ChangedEntities.SPAWN_EGGS.get(entity)).getId();
    }

    private static final HashMap<ResourceLocation, ResourceLocation> ENTITY_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("aerosol_latex_wolf"), ChangedEntities.GAS_WOLF.getId());
        map.put(Changed.modResource("dark_latex_dragon"), ChangedEntities.DARK_DRAGON.getId());
        map.put(Changed.modResource("latex_beifeng"), ChangedEntities.BEIFENG.getId());
        map.put(Changed.modResource("latex_crystal_wolf"), ChangedEntities.CRYSTAL_WOLF.getId());
        map.put(Changed.modResource("latex_crystal_wolf_horned"), ChangedEntities.CRYSTAL_WOLF_HORNED.getId());
        map.put(Changed.modResource("latex_sniper_dog"), ChangedEntities.SNIPER_DOG.getId());
        map.put(Changed.modResource("light_latex_centaur"), ChangedEntities.WHITE_LATEX_CENTAUR.getId());
        map.put(Changed.modResource("light_latex_knight"), ChangedEntities.WHITE_LATEX_KNIGHT.getId());
        map.put(Changed.modResource("light_latex_knight_fusion"), ChangedEntities.WHITE_LATEX_KNIGHT_FUSION.getId());
        map.put(Changed.modResource("light_latex_wolf_organic"), ChangedEntities.WHITE_WOLF_MALE.getId());
        map.put(Changed.modResource("light_latex_wolf_male"), ChangedEntities.WHITE_LATEX_WOLF_MALE.getId());
        map.put(Changed.modResource("light_latex_wolf_female"), ChangedEntities.WHITE_LATEX_WOLF_FEMALE.getId());
        map.put(Changed.modResource("white_latex_wolf"), ChangedEntities.PURE_WHITE_LATEX_WOLF.getId());
    });

    private static final HashMap<ResourceLocation, ResourceLocation> ITEM_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("aerosol_latex_wolf_spawn_egg"), spawnEggHelper(ChangedEntities.GAS_WOLF));
        map.put(Changed.modResource("dark_latex_dragon_spawn_egg"), spawnEggHelper(ChangedEntities.DARK_DRAGON));
        map.put(Changed.modResource("latex_beifeng_spawn_egg"), spawnEggHelper(ChangedEntities.BEIFENG));
        map.put(Changed.modResource("latex_crystal_wolf_spawn_egg"), spawnEggHelper(ChangedEntities.CRYSTAL_WOLF));
        map.put(Changed.modResource("latex_crystal_wolf_horned_spawn_egg"), spawnEggHelper(ChangedEntities.CRYSTAL_WOLF_HORNED));
        map.put(Changed.modResource("latex_sniper_dog_spawn_egg"), spawnEggHelper(ChangedEntities.SNIPER_DOG));
        map.put(Changed.modResource("light_latex_centaur_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_LATEX_CENTAUR));
        map.put(Changed.modResource("light_latex_knight_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_LATEX_KNIGHT));
        map.put(Changed.modResource("light_latex_knight_fusion_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_LATEX_KNIGHT_FUSION));
        map.put(Changed.modResource("light_latex_wolf_organic_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_WOLF_MALE));
        map.put(Changed.modResource("light_latex_wolf_male_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_LATEX_WOLF_MALE));
        map.put(Changed.modResource("light_latex_wolf_female_spawn_egg"), spawnEggHelper(ChangedEntities.WHITE_LATEX_WOLF_FEMALE));
        map.put(Changed.modResource("white_latex_wolf_spawn_egg"), spawnEggHelper(ChangedEntities.PURE_WHITE_LATEX_WOLF));

        map.put(Changed.modResource("latex_beifeng_crystal_fragment"), ChangedItems.BEIFENG_CRYSTAL_FRAGMENT.getId());
        map.put(Changed.modResource("dark_latex_dragon_crystal_fragment"), ChangedItems.DARK_DRAGON_CRYSTAL_FRAGMENT.getId());
    });

    private static final HashMap<ResourceLocation, ResourceLocation> BLOCK_ID_REMAP = Util.make(new HashMap<>(), map -> {

    });

    // IDs that both blocks and items share
    private static final HashMap<ResourceLocation, ResourceLocation> BLOCK_ITEM_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("dark_latex_dragon_crystal"), ChangedBlocks.DARK_DRAGON_CRYSTAL.getId());
        map.put(Changed.modResource("latex_beifeng_crystal"), ChangedBlocks.BEIFENG_CRYSTAL.getId());
        map.put(Changed.modResource("latex_beifeng_crystal_small"), ChangedBlocks.BEIFENG_CRYSTAL_SMALL.getId());
        map.put(Changed.modResource("latex_wolf_crystal_block"), ChangedBlocks.WOLF_CRYSTAL_BLOCK.getId());
        map.put(Changed.modResource("latex_wolf_crystal"), ChangedBlocks.WOLF_CRYSTAL.getId());
        map.put(Changed.modResource("latex_wolf_crystal_small"), ChangedBlocks.WOLF_CRYSTAL_SMALL.getId());
        map.put(Changed.modResource("light_latex_puddle_female"), ChangedBlocks.WHITE_LATEX_PUDDLE_FEMALE.getId());
        map.put(Changed.modResource("light_latex_puddle_male"), ChangedBlocks.WHITE_LATEX_PUDDLE_MALE.getId());
    });

    private static final HashMap<ResourceLocation, ResourceLocation> VARIANT_ID_REMAP = Util.make(new HashMap<>(), map -> {
        map.put(Changed.modResource("form_aerosol_latex_wolf"), ChangedTransfurVariants.GAS_WOLF.getId());
        map.put(Changed.modResource("form_dark_latex_dragon"), ChangedTransfurVariants.DARK_DRAGON.getId());
        map.put(Changed.modResource("form_latex_beifeng"), ChangedTransfurVariants.BEIFENG.getId());
        map.put(Changed.modResource("form_latex_crystal_wolf"), ChangedTransfurVariants.CRYSTAL_WOLF.getId());
        map.put(Changed.modResource("form_latex_crystal_wolf_horned"), ChangedTransfurVariants.CRYSTAL_WOLF_HORNED.getId());
        map.put(Changed.modResource("form_latex_sniper_dog"), ChangedTransfurVariants.SNIPER_DOG.getId());
        map.put(Changed.modResource("form_light_latex_centaur"), ChangedTransfurVariants.WHITE_LATEX_CENTAUR.getId());
        map.put(Changed.modResource("form_light_latex_knight"), ChangedTransfurVariants.WHITE_LATEX_KNIGHT.getId());
        map.put(Changed.modResource("form_light_latex_knight_fusion"), ChangedTransfurVariants.WHITE_LATEX_KNIGHT_FUSION.getId());
        map.put(Changed.modResource("form_light_latex_wolf_organic"), ChangedTransfurVariants.WHITE_WOLF_MALE.getId());
        map.put(Changed.modResource("form_light_latex_wolf/male"), ChangedTransfurVariants.WHITE_LATEX_WOLF_MALE.getId());
        map.put(Changed.modResource("form_light_latex_wolf/female"), ChangedTransfurVariants.WHITE_LATEX_WOLF_FEMALE.getId());
        map.put(Changed.modResource("form_white_latex_wolf"), ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.getId());
    });

    //TODO: add remap for latex syringes and arrows

    private static final HashMap<String, String> ENUM_REMAP = Util.make(new HashMap<>(), map -> {
        map.put("DARK_LATEX", LatexType.DARK_LATEX.name());
        map.put("WHITE_LATEX", LatexType.WHITE_LATEX.name());
    });

    private static final HashMap<String, String> TAG_REMAP = Util.make(new HashMap<>(), map -> {
        map.put("LatexVariant", "TransfurVariant");
        map.put("LatexVariantAge", "TransfurVariantAge");
        map.put("LatexAbilities", "TransfurAbilities");
        map.put("LatexData", "TransfurData");
    });

    private static ResourceLocation updateID(@NotNull Map<ResourceLocation, ResourceLocation> remap, ResourceLocation id) {
        return remap.getOrDefault(id, id);
    }

    private static String updateName(@NotNull Map<String, String> remap, String name) {
        return remap.getOrDefault(name, name);
    }

    private static void updateTagNames(@NotNull CompoundTag tag) {
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

    private static void updateID(@NotNull Map<ResourceLocation, ResourceLocation> remap, @NotNull CompoundTag tag, String idName) {
        if (!tag.contains(idName)) return;
        final ResourceLocation id = ResourceLocation.tryParse(tag.getString(idName));
        if (id != null)
            tag.putString(idName, updateID(remap, id).toString());
    }

    private static void updateName(@NotNull Map<String, String> remap, @NotNull CompoundTag tag, String idName) {
        if (!tag.contains(idName)) return;
        final String id = tag.getString(idName);
        tag.putString(idName, updateName(remap, id));
    }

    private static void updateEntity(@NotNull CompoundTag entityTag) {
        updateID(ENTITY_ID_REMAP, entityTag, "id");
    }

    private static void updateBlockEntity(@NotNull CompoundTag entityTag) {
        updateTagNames(entityTag);
        updateID(ENTITY_ID_REMAP, entityTag, "id");

        updateName(ENUM_REMAP, entityTag, "LatexType");
    }

    private static void updateBlock(@NotNull CompoundTag blockTag) {
        updateID(BLOCK_ID_REMAP, blockTag, "Name");
        updateID(BLOCK_ITEM_ID_REMAP, blockTag, "Name");
    }

    private static void updateItemTag(@NotNull CompoundTag itemTag) {
        updateID(VARIANT_ID_REMAP,  itemTag, "form");
    }

    private static void updateItem(@NotNull CompoundTag itemStack) {
        updateID(ITEM_ID_REMAP, itemStack, "id");
        updateID(BLOCK_ITEM_ID_REMAP, itemStack, "id");
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
        map.put(DataFixTypes.CHUNK, tag -> {
            if (tag.get("block_entities") instanceof ListTag listTag) {
                listTag.forEach(entityTag -> {
                    if (entityTag instanceof CompoundTag compoundTag)
                        updateBlockEntity(compoundTag);
                });
            }
            if (tag.get("sections") instanceof ListTag listTag) {
                listTag.forEach(sectionTag -> {
                    if (sectionTag instanceof CompoundTag compoundTag)
                        if (compoundTag.get("block_states") instanceof CompoundTag stateTag)
                            if (stateTag.get("palette") instanceof ListTag paletteTag)
                                paletteTag.forEach(blockTag -> {
                                    if (blockTag instanceof CompoundTag compoundBlockTag)
                                        updateBlock(compoundBlockTag);
                                });
                });
            }
        });
    });

    private static final Consumer<CompoundTag> NULL_OP = tag -> {};

    public static void updateCompoundTag(@NotNull DataFixTypes type, @Nullable CompoundTag tag) {
        if (tag == null)
            return;

        DATA_FIXERS.getOrDefault(type, NULL_OP).accept(tag);
    }

    public static <T extends Comparable<T>> Optional<T> updateBlockState(@NotNull Property<T> property, String value) {
        if (property instanceof EnumProperty<?>) {
            return property.getValue(updateName(ENUM_REMAP, value.toUpperCase()).toLowerCase());
        }

        return Optional.empty();
    }
}
