package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
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
        public static final TagKey<EntityType<?>> ORGANIC_LATEX = create("organic_latex");
        public static final TagKey<EntityType<?>> PALE_SMALL_EXPOSURE = create("pale_small_exposure");
        public static final TagKey<EntityType<?>> PALE_LARGE_EXPOSURE = create("pale_large_exposure");
        public static final TagKey<EntityType<?>> WHITE_LATEX_SWIMMING = create("white_latex_swimming");
        public static final TagKey<EntityType<?>> PUDDING = create("pudding");
        public static final TagKey<EntityType<?>> ARMLESS = create("armless");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Changed.modResource(name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> LATEX_NON_REPLACEABLE = create("latex_non_replaceable");
        public static final TagKey<Block> GROWS_LATEX_CRYSTALS = create("grows_latex_crystal");
        public static final TagKey<Block> LASER_TRANSLUCENT = create("laser_translucent");
        public static final TagKey<Block> DUCT_CONNECTOR = create("duct_connector");
        public static final TagKey<Block> DUCT_EXIT = create("duct_exit");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registry.BLOCK_REGISTRY, Changed.modResource(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TSC_WEAPON = create("tsc_weapon");

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

    public static class LatexVariants {
        public static final TagKey<LatexVariant<?>> WOLF_LIKE = create("wolf_like");
        public static final TagKey<LatexVariant<?>> SHARK_LIKE = create("shark_like");
        public static final TagKey<LatexVariant<?>> CAT_LIKE = create("cat_like");
        public static final TagKey<LatexVariant<?>> LEOPARD_LIKE = create("leopard_like");

        private static TagKey<LatexVariant<?>> create(String name) {
            return TagKey.create(ChangedRegistry.LATEX_VARIANT.key, Changed.modResource(name));
        }
    }
}
