package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.UniqueEffect;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UseVariantEffectAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("use_variant_ability");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return variant.getLatexEntity() instanceof UniqueEffect;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) {
        return !(player.containerMenu instanceof AbilityRadialMenu);
    }

    @Override
    public void tick(Player player, LatexVariant<?> variant) {
        super.tick(player, variant);
        if (variant.getLatexEntity() instanceof UniqueEffect uniqueEffect)
            uniqueEffect.effectTick(player.level, player);
    }

    @Override
    public ResourceLocation getTexture(Player player, LatexVariant<?> variant) {
        if (variant.getLatexEntity() instanceof UniqueEffect uniqueEffect)
            return new ResourceLocation(getId().getNamespace(), "textures/abilities/" + uniqueEffect.getEffectName() + ".png");
        return super.getTexture(player, variant);
    }
}
