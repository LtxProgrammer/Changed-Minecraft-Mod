package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SwitchGenderAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("switch_gender");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return variant.getLatexEntity() instanceof GenderedEntity;
    }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        super.startUsing(player, variant);

        float beforeHealth = player.getHealth();
        var newVariantId = Gender.switchGenderedForm(variant.getFormId());
        if (!newVariantId.equals(variant.getFormId())) {
            var newVariant = LatexVariant.ALL_LATEX_FORMS.get(newVariantId);
            ProcessTransfur.setPlayerLatexVariant(player, newVariant);
            ChangedSounds.broadcastSound(player, newVariant.sound, 1, 1);
        }
        player.setHealth(beforeHealth);
    }
}
