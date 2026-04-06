package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record ImmediateTransfurDecision<T extends ChangedEntity>(TransfurVariant<T> transfurVariant,
                                                                 TransfurCause cause,
                                                                 @Nullable IAbstractChangedEntity source,
                                                                 boolean initialKeepConscious,
                                                                 Consumer<IAbstractChangedEntity> postTransfurListener) {

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> unsafe(TransfurVariant<T> transfurVariant,
                                                                                TransfurCause cause) {
        return unsafe(transfurVariant, cause, null, entity -> {});
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> unsafe(TransfurVariant<T> transfurVariant,
                                                                                TransfurCause cause,
                                                                                Consumer<IAbstractChangedEntity> postTransfurListener) {
        return unsafe(transfurVariant, cause, null, postTransfurListener);
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> unsafe(TransfurVariant<T> transfurVariant,
                                                                                TransfurCause cause,
                                                                                @Nullable IAbstractChangedEntity source) {
        return unsafe(transfurVariant, cause, source, entity -> {});
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> unsafe(TransfurVariant<T> transfurVariant,
                                                                                TransfurCause cause,
                                                                                @Nullable IAbstractChangedEntity source,
                                                                                Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new ImmediateTransfurDecision<>(transfurVariant, cause, source, false, postTransfurListener);
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> safe(TransfurVariant<T> transfurVariant,
                                                                              TransfurCause cause) {
        return safe(transfurVariant, cause, null, entity -> {});
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> safe(TransfurVariant<T> transfurVariant,
                                                                              TransfurCause cause,
                                                                              Consumer<IAbstractChangedEntity> postTransfurListener) {
        return safe(transfurVariant, cause, null, postTransfurListener);
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> safe(TransfurVariant<T> transfurVariant,
                                                                              TransfurCause cause,
                                                                              @Nullable IAbstractChangedEntity source) {
        return safe(transfurVariant, cause, source, entity -> {});
    }

    public static <T extends ChangedEntity> ImmediateTransfurDecision<T> safe(TransfurVariant<T> transfurVariant,
                                                                              TransfurCause cause,
                                                                              @Nullable IAbstractChangedEntity source,
                                                                              Consumer<IAbstractChangedEntity> postTransfurListener) {
        return new ImmediateTransfurDecision<>(transfurVariant, cause, source, true, postTransfurListener);
    }

    public AssimilationBehavior transfurTargetBehavior(LivingEntity target) {
        final var sourceEntity = source != null ? source.getEntity() : null;
        return AssimilationBehavior.instant(target.level(), () -> {
            var newEntity = transfurVariant.replaceEntity(target, sourceEntity);
            ChangedSounds.broadcastSound(newEntity.getEntity(), transfurVariant.sound, 1.0f, 1.0f);
            postTransfurListener.accept(newEntity);
            return newEntity;
        });
    }

    public ImmediateTransfurDecision<?> withTransfurVariant(TransfurVariant<?> newTransfurVariant) {
        return new ImmediateTransfurDecision<>(newTransfurVariant, cause, source, initialKeepConscious, postTransfurListener);
    }

    public ImmediateTransfurDecision<T> appendTransfurListener(Consumer<IAbstractChangedEntity> listener) {
        return new ImmediateTransfurDecision<>(transfurVariant, cause, source, initialKeepConscious, postTransfurListener.andThen(listener));
    }
}
