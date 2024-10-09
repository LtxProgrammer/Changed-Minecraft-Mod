package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.world.entity.MobCategory;

public class ChangedMobCategories {
    public static final MobCategory CHANGED = MobCategory.create("CHANGED",
            Changed.modResourceStr("changed"), 10, false, false, 128);
    public static final MobCategory UNDERGROUND = MobCategory.create("CHANGED_UNDERGROUND",
            Changed.modResourceStr("underground"), 5, false, false, 128);
    public static final MobCategory AQUATIC = MobCategory.create("CHANGED_AQUATIC",
            Changed.modResourceStr("aquatic"), 5, false, false, 128);
}
