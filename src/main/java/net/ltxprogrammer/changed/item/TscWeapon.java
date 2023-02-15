package net.ltxprogrammer.changed.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TscWeapon extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public TscWeapon(Properties properties) {
        super(properties.tab(ChangedTabs.TAB_CHANGED_COMBAT));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed(), AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return !player.isCreative();
    }

    public int attackStun() { return 0; }
    public abstract double attackDamage();
    public abstract double attackSpeed();
    public double attackRange() {
        return 1.0;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public void sweepWeapon(LivingEntity source) {
        double d0 = (double)(-Mth.sin(source.getYRot() * ((float)Math.PI / 180F))) * attackRange();
        double d1 = (double)Mth.cos(source.getYRot() * ((float)Math.PI / 180F)) * attackRange();
        if (source.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ChangedParticles.TSC_SWEEP_ATTACK, source.getX() + d0, source.getY(0.5D), source.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }
    }

    public void applyShock(LivingEntity enemy) {
        ChangedSounds.broadcastSound(enemy, ChangedSounds.PARALYZE1, 1, 1);
        enemy.addEffect(new MobEffectInstance(ChangedEffects.SHOCK, attackStun(), 0, false, false, true));
    }
}
