package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

// A simple ability ditches the instantiation part of abilities, in order to be simpler to write
public abstract class SimpleAbility extends AbstractAbility<SimpleAbilityInstance> {
    public SimpleAbility() {
        super(SimpleAbilityInstance::new);
    }
}
