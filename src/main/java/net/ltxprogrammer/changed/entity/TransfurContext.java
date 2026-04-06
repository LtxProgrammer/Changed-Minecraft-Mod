package net.ltxprogrammer.changed.entity;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record TransfurContext(TransfurCause cause,
                              @Nullable Either<IAbstractChangedEntity, ILatexAssimilatedEntity> source) {

    public TransfurContext withCause(TransfurCause cause) {
        return new TransfurContext(cause, source);
    }

    public TransfurContext withSource(@Nullable IAbstractChangedEntity source) {
        return new TransfurContext(cause, source == null ? null : Either.left(source));
    }

    public TransfurContext withSource(@Nullable ILatexAssimilatedEntity source) {
        return new TransfurContext(cause, source == null ? null : Either.right(source));
    }

    public TransfurContext withoutSource() {
        return new TransfurContext(cause, null);
    }

    @Deprecated
    public static TransfurContext playerLatexAttack(Player player) {
        return new TransfurContext(
                player.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, Either.left(IAbstractChangedEntity.forPlayer(player))
        );
    }

    @Deprecated
    public static TransfurContext npcLatexAttack(ChangedEntity latex) {
        return new TransfurContext(
                latex.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, Either.left(IAbstractChangedEntity.forEntity(latex))
        );
    }

    public static TransfurContext playerLatexHazard(@Nullable Player player, TransfurCause cause) {
        return new TransfurContext(cause, player == null ? null : Either.left(IAbstractChangedEntity.forPlayer(player)));
    }

    public static TransfurContext npcLatexHazard(@Nullable ChangedEntity latex, TransfurCause cause) {
        return new TransfurContext(cause, latex == null ? null : Either.left(IAbstractChangedEntity.forEntity(latex)));
    }

    public static TransfurContext latexHazard(@Nullable IAbstractChangedEntity entity, TransfurCause cause) {
        return new TransfurContext(cause, entity == null ? null : Either.left(entity));
    }

    public static TransfurContext latexHazard(@Nullable ILatexAssimilatedEntity entity, TransfurCause cause) {
        return new TransfurContext(cause, entity == null ? null : Either.right(entity));
    }

    public static TransfurContext hazard(TransfurCause cause) {
        return new TransfurContext(cause, null);
    }

    public static TransfurContext fromTag(CompoundTag tag, @Nullable Level level) {
        TransfurCause cause = TransfurCause.ATTACK_REPLICATE_LEFT;
        try {
            cause = TransfurCause.valueOf(tag.getString("cause"));
        } catch (Exception ignored) {
        }
        int id = tag.getInt("source");
        if (id != -1 && level != null && level.getEntity(id) instanceof LivingEntity livingEntity) {
            IAbstractChangedEntity source = IAbstractChangedEntity.forEither(livingEntity);
            if (source != null)
                return new TransfurContext(cause, Either.left(source));

            ILatexAssimilatedEntity source2 = ILatexAssimilatedEntity.forEntity(livingEntity);
            if (source2 != null)
                return new TransfurContext(cause, Either.right(source2));
        }

        return new TransfurContext(cause, null);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("cause", cause.name());
        tag.putInt("source", source == null ? -1 :
                source.map(IAbstractChangedEntity::getId, ILatexAssimilatedEntity::getId));
        return tag;
    }

    public boolean isFromPlayer() {
        return source != null && source.left().isPresent() && source.left().get().isPlayer();
    }
}
