package net.ltxprogrammer.changed.recipe;

import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.CompactDisc;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RecipeDisks {
    public static CompoundTag diskHelper(String title, String author, String data, boolean translate) {
        CompoundTag value = new CompoundTag();
        value.putString(CompactDisc.TAG_TITLE, title);
        value.putString(CompactDisc.TAG_AUTHOR, author);
        value.putString(CompactDisc.TAG_DATA, data);
        value.putBoolean(CompactDisc.TAG_TRANSLATE, translate);
        return value;
    }

    public static CompoundTag diskHelper(String disk, String author) {
        return diskHelper("changed.compact_disc.title." + disk, author, "changed.compact_disc.content." + disk, true);
    }

    public static ItemStack createDisk(String disk, String author) {
        return new ItemStack(ChangedItems.COMPACT_DISC.get(), 1, diskHelper(disk, author));
    }

    public static Supplier<ItemStack> register(Supplier<ItemStack> supplier) {
        DISK_ITEM_POOL.add(supplier);
        return supplier;
    }

    public static final List<Supplier<ItemStack>> DISK_ITEM_POOL = new ArrayList<>();
    public static final Supplier<ItemStack> DISK_AEROSOL_LATEX_WOLF = register(() -> createDisk("aerosol_latex_wolf", "Dr. McMillian"));

    public static final Supplier<ItemStack> DISK_LIGHT_LATEX_WOLF = register(() -> createDisk("light_latex_wolf", "Dr. K"));

    public static final Supplier<ItemStack> DISK_DARK_LATEX_WOLF = register(() -> createDisk("dark_latex_wolf", "Dr. Kazuma"));
    public static final Supplier<ItemStack> DISK_DARK_LATEX_YUFENG = register(() -> createDisk("dark_latex_yufeng", "Dr. Kazuma"));
    public static final Supplier<ItemStack> DISK_DARK_LATEX_DRAGON = register(() -> createDisk("dark_latex_dragon", "Dr. Kazuma"));

    public static final Supplier<ItemStack> DISK_LATEX_BEIFENG = register(() -> createDisk("latex_beifeng", "Dr. Gent"));

    public static final Supplier<ItemStack> DISK_LATEX_SHARK = register(() -> createDisk("latex_shark", "Dr. Grey"));
    public static final Supplier<ItemStack> DISK_LATEX_TIGER_SHARK = register(() -> createDisk("latex_tiger_shark", "Dr. Grey"));
    public static final Supplier<ItemStack> DISK_LATEX_ORCA = register(() -> createDisk("latex_orca", "Dr. Grey"));
    public static final Supplier<ItemStack> DISK_LATEX_SQUID_DOG = register(() -> createDisk("latex_squid_dog", "Dr. Hawk"));

    public static final Supplier<ItemStack> DISK_WHITE_LATEX_CREATURE = register(() -> createDisk("white_latex_creature", "Dr. Thompson"));

    public static final Supplier<ItemStack> DISK_SPECIAL_LATEX = register(() -> createDisk("special_latex", "Dr. Breeze"));

    // /give Dev changed:floppy_disk{"author":AUTHOR,"title":"changed.floppy_disk.title.FORM","data":"changed.floppy_disk.content.FORM","translate":true}
}
