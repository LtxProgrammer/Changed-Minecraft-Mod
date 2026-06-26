package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SimpleCreateItemAbility extends SimpleAbility {
    public SimpleCreateItemAbility(Supplier<ItemStack> itemSupplier, float exhaustion, float minimumHunger) {
        this.itemSupplier = itemSupplier;
        this.exhaustion = exhaustion;
        this.minimumHunger = minimumHunger;
    }

    private final Supplier<ItemStack> itemSupplier;
    private final float exhaustion;
    private final float minimumHunger;

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getFoodLevel() > minimumHunger || entity.isCreative();
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) { return false; }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        var item = itemSupplier.get();
        if (!entity.addItem(item))
            entity.drop(item, false, false);
        if (!entity.isCreative()) {
            entity.causeFoodExhaustion(exhaustion);

            switch (getAbilityLevel(entity)) {
                case 0 -> entity.getEntity().addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 1));
                case 1 -> entity.getEntity().addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 0));
                default -> {}
            }
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 20 * (3 - Mth.clamp(getAbilityLevel(entity), 0, 2));
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 20;
    }
}
