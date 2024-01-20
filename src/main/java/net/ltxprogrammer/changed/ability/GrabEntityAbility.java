package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class GrabEntityAbility extends AbstractAbility<GrabEntityAbilityInstance> {
    public GrabEntityAbility() {
        super(GrabEntityAbilityInstance::new);
    }

    public static boolean isEntityNoControl(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            var ability = AbstractAbility.getAbilityInstance(livingEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability != null && ability.grabbedHasControl)
                return false;
        }

        if (entity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            return ability != null && !ability.grabbedHasControl;
        }

        return false;
    }
}
