package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

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

    public static @Nullable IAbstractChangedEntity getGrabber(LivingEntity grabbed) {
        if (!(grabbed instanceof LivingEntityDataExtension ext)) return null;

        return IAbstractChangedEntity.forEither(ext.getGrabbedBy());
    }

    public static Optional<IAbstractChangedEntity> getGrabberSafe(LivingEntity grabbed) {
        if (!(grabbed instanceof LivingEntityDataExtension ext)) return Optional.empty();

        return Optional.ofNullable(IAbstractChangedEntity.forEither(ext.getGrabbedBy()));
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(new TranslatableComponent("ability.changed.grab_entity.desc"));

    @NotNull
    public static LivingEntity getControllingEntity(LivingEntity livingEntity) {
        var grabbedByEntity = getGrabberSafe(livingEntity)
                .flatMap(changedEntity -> changedEntity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                .map(ability -> ability.grabbedHasControl ? livingEntity : ability.entity.getEntity());

        return grabbedByEntity.orElseGet(() -> AbstractAbility.getAbilityInstanceSafe(livingEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .map(ability -> ability.grabbedHasControl ? ability.grabbedEntity : livingEntity)
                .orElse(livingEntity));
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
