package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedDamageSources {
    public record DamageTypeHolder(ResourceKey<DamageType> key) {
        public DamageSource source(RegistryAccess access) {
            final Holder<DamageType> type = access.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
            return new DamageSource(type);
        }

        public DamageSource source(RegistryAccess access, Entity sourceEntity) {
            final Holder<DamageType> type = access.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
            return new DamageSource(type, sourceEntity);
        }
    }

    private static DamageTypeHolder holder(String name) {
        return new DamageTypeHolder(ResourceKey.create(Registries.DAMAGE_TYPE, Changed.modResource(name)));
    }

    public static final DamageTypeHolder TRANSFUR = holder("transfur");
    public static final DamageTypeHolder ABSORB = holder("absorb");
    public static final DamageTypeHolder BLOODLOSS = holder("bloodloss");
    public static final DamageTypeHolder ELECTROCUTION = holder("electrocution");
    public static final DamageTypeHolder WHITE_LATEX = holder("white_latex");
    public static final DamageTypeHolder LATEX_FLUID = holder("latex_fluid");
    public static final DamageTypeHolder PALE = holder("pale");
    public static final DamageTypeHolder FAN = holder("fan");
    public static final DamageTypeHolder HEART_ATTACK = holder("heart_attack");
    public static final DamageTypeHolder GRAB_ESCAPE = holder("grab_escape");
    public static final DamageTypeHolder GALE_WIND_BURST = holder("gale_wind_burst");
    public static final DamageTypeHolder MANTLE_STOP = holder("mantle_stop");
    public static final DamageTypeHolder MER_TAIL_WHIP = holder("mer_tail_whip");

    public static DamageSource entityTransfur(RegistryAccess access, LivingEntity source) {
        return TRANSFUR.source(access, source);
    }

    public static DamageSource entityTransfur(RegistryAccess access, @Nullable IAbstractChangedEntity source) {
        return TRANSFUR.source(access, source == null ? null : source.getEntity());
    }

    public static DamageSource entityAbsorb(RegistryAccess access, LivingEntity source) {
        return ABSORB.source(access, source);
    }

    public static DamageSource entityAbsorb(RegistryAccess access, @Nullable IAbstractChangedEntity source) {
        return ABSORB.source(access, source == null ? null : source.getEntity());
    }
}
