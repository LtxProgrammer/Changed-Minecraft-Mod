package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;

public class AccessChestAbility extends AbstractAbility<AccessChestAbilityInstance> {
    public AccessChestAbility() {
        super(AccessChestAbilityInstance::new);
    }

    @Override
    public ResourceLocation getId() {
        return Changed.modResource("access_chest");
    }
}
