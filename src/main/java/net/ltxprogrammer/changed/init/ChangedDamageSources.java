package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.world.damagesource.DamageSource;

public class ChangedDamageSources {
    public static final DamageSource TRANSFUR = (new DamageSource(Changed.modResourceStr("transfur"))).bypassArmor().bypassMagic();
    public static final DamageSource WHITE_LATEX = (new DamageSource(Changed.modResourceStr("white_latex"))).bypassArmor().bypassMagic();
    public static final DamageSource PALE = (new DamageSource(Changed.modResourceStr("pale"))).bypassArmor().bypassMagic();
}
