package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.EntityAssimilationBehavior;
import net.ltxprogrammer.changed.entity.ai.TransfurDecider;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILatexAssimilatedEntity {
    @NotNull LivingEntity getEntity();
    @NotNull Level getLevel();
    int getId();

    boolean isDeadOrDying();

    default @NotNull TransfurContext replicate() {
        return TransfurContext.latexHazard(this, TransfurCause.GRAB_REPLICATE);
    }
    default @NotNull TransfurContext absorb() {
        return TransfurContext.latexHazard(this, TransfurCause.GRAB_ABSORB);
    }

    static ILatexAssimilatedEntity forEntity(LivingEntity entity) {
        final var behavior = ProcessTransfur.getEntityAssimilationBehavior(entity);
        if (!(behavior instanceof EntityAssimilationBehavior.InjectEntityWithTransfurGoals<?> transfurGoalsBehavior))
            return null;

        return forEntity(entity, transfurGoalsBehavior.decider);
    }

    static ILatexAssimilatedEntity forEntity(LivingEntity entity, @Nullable TransfurDecider<?> decider) {
        if (decider == null)
            return null;
        if (entity instanceof Player)
            return null;
        if (entity instanceof ChangedEntity)
            return null;

        return new ILatexAssimilatedEntity() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return entity;
            }

            @Override
            public @NotNull Level getLevel() {
                return entity.level();
            }

            @Override
            public int getId() {
                return entity.getId();
            }

            @Override
            public boolean isDeadOrDying() {
                return entity.isDeadOrDying();
            }
        };
    }
}
