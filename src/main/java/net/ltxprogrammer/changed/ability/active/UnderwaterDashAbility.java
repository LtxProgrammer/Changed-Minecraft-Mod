package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeMod;

import java.util.Collection;
import java.util.Collections;

public class UnderwaterDashAbility extends AbstractAbility<UnderwaterDashAbilityInstance> {
    public UnderwaterDashAbility() {
        super(UnderwaterDashAbilityInstance::new);
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return entity.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get()) ? UseType.CHARGE_TIME : UseType.INSTANT;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 4;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 5 * 20;
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return entity.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get()) ? super.getAbilityName(entity) : Component.translatable("ability.changed.underwater_dash.alt");
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.underwater_dash.desc"));
    private static final Collection<Component> DESCRIPTION_ALT = Collections.singleton(Component.translatable("ability.changed.underwater_dash.desc_alt"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return entity.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get()) ? DESCRIPTION : DESCRIPTION_ALT;
    }
}
