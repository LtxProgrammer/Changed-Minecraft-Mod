package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ChangedTags {
    public static class EntityTypes {
        public static final TagKey<EntityType<?>> HUMANOIDS = create("humanoids");
        public static final TagKey<EntityType<?>> LATEX = create("latexes");
        public static final TagKey<EntityType<?>> PARTIAL_LATEX = create("partial_latexes");
        @Deprecated public static final TagKey<EntityType<?>> ORGANIC_LATEX = create("organic_latex");
        public static final TagKey<EntityType<?>> PALE_SMALL_EXPOSURE = create("pale_small_exposure");
        public static final TagKey<EntityType<?>> PALE_LARGE_EXPOSURE = create("pale_large_exposure");
        public static final TagKey<EntityType<?>> WHITE_LATEX_SWIMMING = create("white_latex_swimming");
        public static final TagKey<EntityType<?>> PUDDING = create("pudding");
        public static final TagKey<EntityType<?>> ARMLESS = create("armless");
        public static final TagKey<EntityType<?>> CANNOT_OPEN_LAB_DOORS = create("cannot_open_lab_doors");
        public static final TagKey<EntityType<?>> CAN_OPEN_LAB_DOORS = create("can_open_lab_doors");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Changed.modResource(name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> GROWS_LATEX_CRYSTALS = create("grows_latex_crystal");
        public static final TagKey<Block> LASER_TRANSLUCENT = create("laser_translucent");
        public static final TagKey<Block> DUCT_CONNECTOR = create("duct_connector");
        public static final TagKey<Block> DUCT_EXIT = create("duct_exit");
        public static final TagKey<Block> GAS = create("gas");
        public static final TagKey<Block> AIR_CONDITIONER = create("air_conditioner");
        public static final TagKey<Block> LATEX_CRYSTAL = create("latex_crystal");
        public static final TagKey<Block> LATEX_SPAWNABLE_ON = create("latex_spawnable_on");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registry.BLOCK_REGISTRY, Changed.modResource(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TSC_WEAPON = create("tsc_weapon");
        public static final TagKey<Item> WILL_BREAK_ON_TF = create("will_break_on_tf");
        public static final TagKey<Item> UPPER_ABDOMEN_ARMOR = create("upper_abdomen_armor");
        public static final TagKey<Item> LOWER_ABDOMEN_ARMOR = create("lower_abdomen_armor");
        public static final TagKey<Item> QUADRUPEDAL_LEGGINGS = create("quadrupedal_leggings");
        public static final TagKey<Item> QUADRUPEDAL_BOOTS = create("quadrupedal_boots");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registry.ITEM_REGISTRY, Changed.modResource(name));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> LATEX = create("latex");

        private static TagKey<Fluid> create(String name) {
            return TagKey.create(Registry.FLUID_REGISTRY, Changed.modResource(name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> HAS_BEEHIVES = create("has_structure/beehives");
        public static final TagKey<Biome> HAS_DECAYED_LABS = create("has_structure/decayed_labs");
        public static final TagKey<Biome> HAS_DARK_DECAYED_LABS = create("has_structure/dark_decayed_labs");
        public static final TagKey<Biome> HAS_WHITE_DECAYED_LABS = create("has_structure/white_decayed_labs");
        public static final TagKey<Biome> HAS_FACILITY = create("has_structure/facility");

        private static TagKey<Biome> create(String name) {
            return TagKey.create(Registry.BIOME_REGISTRY, Changed.modResource(name));
        }
    }

    public static class TransfurVariants {
        public static final TagKey<TransfurVariant<?>> CAN_SLEEP_ON_PILLOWS = create("can_sleep_on_pillows");
        public static final TagKey<TransfurVariant<?>> BREAK_ITEMS_ON_TF = create("break_items_on_tf");
        public static final TagKey<TransfurVariant<?>> TEMPORARY_ONLY = create("temporary_only");

        private static TagKey<TransfurVariant<?>> create(String name) {
            return TagKey.create(ChangedRegistry.TRANSFUR_VARIANT.key, Changed.modResource(name));
        }
    }
}
