package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.VisionType;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.Collections;

public class ToggleWaveVisionAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity.getEntity()), (player, variant) -> {
            variant.visionType = variant.visionType == VisionType.NORMAL ? VisionType.WAVE_VISION : VisionType.NORMAL;
        });
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 60;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(new TranslatableComponent("ability.changed.toggle_wave_vision.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
