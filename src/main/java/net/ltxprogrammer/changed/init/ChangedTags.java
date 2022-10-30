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

        private static TagKey<EntityType<?>> create(String p_203849_) {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(p_203849_));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> LATEX_NON_REPLACEABLE = create(Changed.modResourceStr("latex_non_replaceable"));

        private static TagKey<Block> create(String p_203849_) {
            return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(p_203849_));
        }
    }
}
