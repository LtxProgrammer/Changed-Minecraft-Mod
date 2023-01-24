package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class ChangedTags {
    public static class EntityTypes {
        public static final TagKey<EntityType<?>> HUMANOIDS = create(Changed.modResourceStr("humanoids"));
        public static final TagKey<EntityType<?>> ORGANIC_LATEX = create(Changed.modResourceStr("organic_latex"));

        private static TagKey<EntityType<?>> create(String p_203849_) {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(p_203849_));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> LATEX_NON_REPLACEABLE = create(Changed.modResourceStr("latex_non_replaceable"));
        public static final TagKey<Block> GROWS_LATEX_CRYSTALS = create(Changed.modResourceStr("grows_latex_crystal"));
        public static final TagKey<Block> LASER_TRANSLUCENT = create(Changed.modResourceStr("laser_translucent"));

        private static TagKey<Block> create(String p_203849_) {
            return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(p_203849_));
        }
    }
}
