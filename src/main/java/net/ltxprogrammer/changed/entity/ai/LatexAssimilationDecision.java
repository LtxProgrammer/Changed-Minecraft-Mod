package net.ltxprogrammer.changed.entity.ai;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The assimilation decided by a latex transfurring source (entity, block, item).
 * @param decisionStrength
 * @param transfurVariant
 * @param postTransfurListener called when the target successfully transfurs into transfurVariant
 */
public record LatexAssimilationDecision<T extends ChangedEntity>(DecisionStrength decisionStrength,
                                                                 Method method,
                                                                 TransfurVariant<T> transfurVariant,
                                                                 TransfurContext context,
                                                                 float transfurProgress,
                                                                 Consumer<IAbstractChangedEntity> postTransfurListener) {
    public enum DecisionStrength {
        LOW,
        HIGH
    }

    public enum Method {
        REPLICATION,
        ABSORPTION
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> weak(Method method, TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return weak(method, transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> weak(Method method, TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new LatexAssimilationDecision<>(DecisionStrength.LOW, method, transfurVariant, context, damage, postTransfurListener);
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> weakReplication(TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return weakReplication(transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> weakReplication(TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new LatexAssimilationDecision<>(DecisionStrength.LOW, Method.REPLICATION, transfurVariant, context, damage, postTransfurListener);
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> weakAbsorption(TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return weakAbsorption(transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> weakAbsorption(TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new LatexAssimilationDecision<>(DecisionStrength.LOW, Method.ABSORPTION, transfurVariant, context, damage, postTransfurListener);
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> fromBlockOrItem(TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return weakAbsorption(transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> fromBlockOrItem(TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return weakAbsorption(transfurVariant, context, damage, postTransfurListener);
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> strong(Method method, TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return strong(method, transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> strong(Method method, TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new LatexAssimilationDecision<>(DecisionStrength.HIGH, method, transfurVariant, context, damage, postTransfurListener);
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> strongReplication(TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return strongReplication(transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> strongReplication(TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new LatexAssimilationDecision<>(DecisionStrength.HIGH, Method.REPLICATION, transfurVariant, context, damage, postTransfurListener);
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> strongAbsorption(TransfurVariant<T> transfurVariant, TransfurContext context, float damage) {
        return strongAbsorption(transfurVariant, context, damage, entity -> {});
    }

    public static <T extends ChangedEntity> LatexAssimilationDecision<T> strongAbsorption(TransfurVariant<T> transfurVariant, TransfurContext context, float damage, Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new LatexAssimilationDecision<>(DecisionStrength.HIGH, Method.ABSORPTION, transfurVariant, context, damage, postTransfurListener);
    }

    public DamageSource getDamageSource(RegistryAccess registryAccess) {
        if (context.source() == null)
            return ChangedDamageSources.entityTransfur(registryAccess, (LivingEntity)null);

        final var sourceEntity = context.source().map(IAbstractChangedEntity::getEntity, ILatexAssimilatedEntity::getEntity);
        if (context.source().right().isPresent())
            return ChangedDamageSources.entityAbsorb(registryAccess, sourceEntity);

        return switch (method) {
            case REPLICATION -> ChangedDamageSources.entityTransfur(registryAccess, sourceEntity);
            case ABSORPTION -> ChangedDamageSources.entityAbsorb(registryAccess, sourceEntity);
        };
    }

    protected AssimilationBehavior transfurByReplication(LivingEntity target, IAbstractChangedEntity transfurSource) {
        return AssimilationBehavior.progressThenTransfur(target,
                this.getDamageSource(target.level().registryAccess()),
                transfurProgress,
                () -> {
                    var newEntity = transfurVariant.replaceEntity(target, transfurSource);
                    ProcessTransfur.onAssimilateEntity(transfurSource);
                    ChangedSounds.broadcastSound(newEntity.getEntity(), transfurVariant.sound, 1.0f, 1.0f);
                    postTransfurListener.accept(newEntity);
                    return newEntity;
                });
    }

    protected AssimilationBehavior transfurByAbsorption(LivingEntity target, IAbstractChangedEntity transfurSource) {
        return AssimilationBehavior.progressThenTransfur(target,
                this.getDamageSource(target.level().registryAccess()),
                transfurProgress,
                () -> {
                    if (target instanceof Player player) {
                        ProcessTransfur.killPlayerByAbsorption(player, transfurSource.getEntity());
                    } else {
                        target.discard();
                    }

                    transfurSource.replaceVariant(transfurVariant);
                    ProcessTransfur.onAbsorbEntity(transfurSource);
                    ChangedSounds.broadcastSound(transfurSource.getEntity(), transfurVariant.sound, 1.0f, 1.0f);
                    postTransfurListener.accept(transfurSource);
                    return transfurSource;
                });
    }

    protected AssimilationBehavior transfurByAbsorption(LivingEntity target, ILatexAssimilatedEntity transfurSource) {
        return AssimilationBehavior.progressThenTransfur(target,
                this.getDamageSource(target.level().registryAccess()),
                transfurProgress,
                () -> {
                    var newEntity = transfurVariant.replaceEntity(target, transfurSource);

                    if (transfurSource.getEntity() instanceof Player player) { // Emergency Player check to prevent discarding player
                        ProcessTransfur.killPlayerByAbsorption(player, transfurSource.getEntity());
                    } else {
                        transfurSource.getEntity().discard();
                    }

                    ProcessTransfur.onAbsorbEntity(newEntity);
                    ChangedSounds.broadcastSound(newEntity.getEntity(), transfurVariant.sound, 1.0f, 1.0f);
                    postTransfurListener.accept(newEntity);
                    return newEntity;
                });
    }

    protected AssimilationBehavior entityLatexAssimilateVictimBehavior(LivingEntity target, Either<IAbstractChangedEntity, ILatexAssimilatedEntity> transfurSource) {
        if (transfurSource.right().isPresent())
            return transfurByAbsorption(target, transfurSource.right().get());

        return switch (method) {
            case REPLICATION -> this.transfurByReplication(target, transfurSource.left().get());
            case ABSORPTION -> this.transfurByAbsorption(target, transfurSource.left().get());
        };
    }

    public AssimilationBehavior latexAssimilateVictimBehavior(LivingEntity target) {
        if (context.source() != null)
            return entityLatexAssimilateVictimBehavior(target, context.source());

        return AssimilationBehavior.progressThenTransfur(target,
                this.getDamageSource(target.level().registryAccess()),
                transfurProgress,
                () -> {
                    var newEntity = transfurVariant.replaceEntity(target, (LivingEntity)null);
                    postTransfurListener.accept(newEntity);
                    return newEntity;
                });
    }

    public LatexAssimilationDecision<?> withTransfurVariant(TransfurVariant<?> newTransfurVariant) {
        return new LatexAssimilationDecision<>(decisionStrength, method, newTransfurVariant, context, transfurProgress, postTransfurListener);
    }

    public LatexAssimilationDecision<T> withTransfurProgress(float newProgress) {
        return new LatexAssimilationDecision<>(decisionStrength, method, transfurVariant, context, newProgress, postTransfurListener);
    }

    public LatexAssimilationDecision<T> appendTransfurListener(Consumer<IAbstractChangedEntity> listener) {
        return new LatexAssimilationDecision<>(decisionStrength, method, transfurVariant, context, transfurProgress, postTransfurListener.andThen(listener));
    }
}
