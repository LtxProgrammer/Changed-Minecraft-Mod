package net.ltxprogrammer.changed.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class TagUtil {
    public static String getStringOrDefault(@Nullable CompoundTag tag, String name, String _default) {
        return tag != null && tag.contains(name) ? tag.getString(name) : _default;
    }

    public static String getStringOrDefault(@Nullable ItemStack item, String name, String _default) {
        return item != null ? getStringOrDefault(item.getTag(), name, _default) : _default;
    }

    public static boolean getBooleanOrDefault(@Nullable CompoundTag tag, String name, boolean _default) {
        return tag != null && tag.contains(name) ? tag.getBoolean(name) : _default;
    }

    public static boolean getBooleanOrDefault(@Nullable ItemStack item, String name, boolean _default) {
        return item != null ? getBooleanOrDefault(item.getTag(), name, _default) : _default;
    }

    public static BlockPos getBlockPos(CompoundTag cTag, String name) {
        ListTag tag = (ListTag)cTag.get(name);
        return new BlockPos(tag.getInt(0), tag.getInt(1), tag.getInt(2));
    }

    public static ResourceLocation getResourceLocation(CompoundTag cTag, String name) {
        return new ResourceLocation(cTag.getString(name));
    }

    public static void putBlockPos(CompoundTag cTag, String name, BlockPos pos) {
        ListTag tag = new ListTag();
        tag.add(IntTag.valueOf(pos.getX()));
        tag.add(IntTag.valueOf(pos.getY()));
        tag.add(IntTag.valueOf(pos.getZ()));
        cTag.put(name, tag);
    }

    public static void putResourceLocation(CompoundTag cTag, String name, ResourceLocation location) {
        cTag.putString(name, location.toString());
    }
}
