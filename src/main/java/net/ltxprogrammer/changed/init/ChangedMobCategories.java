package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.world.entity.MobCategory;

public class ChangedMobCategories {
    public static final MobCategory CHANGED = MobCategory.create("CHANGED",
            Changed.modResourceStr("changed"), 40, false, false, 128);
    public static final MobCategory UNDERGROUND = MobCategory.create("CHANGED_UNDERGROUND",
            Changed.modResourceStr("underground"), 20, false, false, 128);
}
