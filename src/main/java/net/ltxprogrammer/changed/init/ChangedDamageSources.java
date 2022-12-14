package net.ltxprogrammer.changed.init;

import net.minecraft.world.damagesource.DamageSource;

public class ChangedDamageSources {
    public static final DamageSource TRANSFUR = (new DamageSource("transfur")).bypassArmor().bypassMagic();
    public static final DamageSource WHITE_LATEX = (new DamageSource("white_latex")).bypassArmor().bypassMagic();
}
