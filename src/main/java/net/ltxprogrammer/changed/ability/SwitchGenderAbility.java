package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.player.Player;

public class SwitchGenderAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractLatex entity) {
        return entity.getLatexEntity() instanceof GenderedEntity && entity.getEntity() instanceof Player;
    }

    @Override
    public void startUsing(IAbstractLatex entity) {
        super.startUsing(entity);

        ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(entity.getEntity()), (player, variant) -> {
            float beforeHealth = player.getHealth();
            var newVariantId = Gender.switchGenderedForm(variant.getFormId());
            if (!newVariantId.equals(variant.getFormId())) {
                var newVariant = ChangedRegistry.LATEX_VARIANT.get().getValue(newVariantId);
                ProcessTransfur.setPlayerLatexVariant(player, newVariant);
                ChangedSounds.broadcastSound(player, newVariant.sound, 1, 1);
            }
            player.setHealth(beforeHealth);
        });
    }

    @Override
    public UseType getUseType(IAbstractLatex entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractLatex entity) {
        return 60;
    }
}
