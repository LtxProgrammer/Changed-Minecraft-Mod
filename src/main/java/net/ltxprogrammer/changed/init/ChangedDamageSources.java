package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChangedDamageSources {
    public static class TransfurDamageSource extends DamageSource {
        protected final LivingEntity entity;

        public TransfurDamageSource(String name, LivingEntity entity) {
            super(name);
            this.entity = entity;
        }

        public Entity getEntity() {
            return this.entity;
        }

        public String toString() {
            return "TransfurDamageSource (" + this.entity + ")";
        }
    }

    public static DamageSource entityTransfur(LivingEntity source) {
        return new TransfurDamageSource(Changed.modResourceStr("transfur"), source);
    }

    public static final DamageSource BLOODLOSS = (new DamageSource("changed:bloodloss")).bypassArmor();
    public static final DamageSource WHITE_LATEX = (new DamageSource(Changed.modResourceStr("white_latex"))).bypassArmor().bypassMagic();
    public static final DamageSource PALE = (new DamageSource(Changed.modResourceStr("pale"))).bypassArmor().bypassMagic();
    public static final DamageSource FAN = new DamageSource(Changed.modResourceStr("fan"));
}
