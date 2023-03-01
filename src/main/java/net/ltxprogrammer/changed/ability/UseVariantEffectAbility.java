package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.UniqueEffect;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UseVariantEffectAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return variant.getLatexEntity() instanceof UniqueEffect;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) {
        return !(player.containerMenu instanceof AbilityRadialMenu);
    }

    @Override
    public void tick(Player player, LatexVariantInstance<?> variant) {
        super.tick(player, variant);
        if (variant.getLatexEntity() instanceof UniqueEffect uniqueEffect)
            uniqueEffect.effectTick(player.level, player);
    }

    @Override
    public TranslatableComponent getDisplayName(Player player, LatexVariantInstance<?> variant) {
        if (variant.getLatexEntity() instanceof UniqueEffect uniqueEffect)
            return new TranslatableComponent("ability." + getRegistryName().toString().replace(':', '.') + "." + uniqueEffect.getEffectName());
        return super.getDisplayName(player, variant);
    }

    @Override
    public ResourceLocation getTexture(Player player, LatexVariantInstance<?> variant) {
        if (variant.getLatexEntity() instanceof UniqueEffect uniqueEffect)
            return new ResourceLocation(getRegistryName().getNamespace(), "textures/abilities/" + uniqueEffect.getEffectName() + ".png");
        return super.getTexture(player, variant);
    }
}
