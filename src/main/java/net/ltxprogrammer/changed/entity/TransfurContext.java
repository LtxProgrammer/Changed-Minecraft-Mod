package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TransfurContext {
    public final TransfurCause cause;
    public final @Nullable IAbstractChangedEntity source;

    public TransfurContext(TransfurCause cause, @Nullable IAbstractChangedEntity source) {
        this.cause = cause;
        this.source = source;
    }

    public TransfurContext withCause(TransfurCause cause) {
        return new TransfurContext(cause, source);
    }

    public TransfurContext withSource(@Nullable IAbstractChangedEntity source) {
        return new TransfurContext(cause, source);
    }

    @Deprecated
    public static TransfurContext playerLatexAttack(Player player) {
        return new TransfurContext(
                player.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, IAbstractChangedEntity.forPlayer(player)
        );
    }

    @Deprecated
    public static TransfurContext npcLatexAttack(ChangedEntity latex) {
        return new TransfurContext(
                latex.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, IAbstractChangedEntity.forEntity(latex)
        );
    }

    public static TransfurContext playerLatexHazard(Player player, TransfurCause cause) {
        return new TransfurContext(cause, IAbstractChangedEntity.forPlayer(player));
    }

    public static TransfurContext npcLatexHazard(ChangedEntity latex, TransfurCause cause) {
        return new TransfurContext(cause, IAbstractChangedEntity.forEntity(latex));
    }

    public static TransfurContext latexHazard(IAbstractChangedEntity entity, TransfurCause cause) {
        return new TransfurContext(cause, entity);
    }

    public static TransfurContext hazard(TransfurCause cause) {
        return new TransfurContext(cause, null);
    }

    public static TransfurContext fromTag(CompoundTag tag, @Nullable Level level) {
        TransfurCause cause = TransfurCause.ATTACK_REPLICATE_LEFT;
        try {
            cause = TransfurCause.valueOf(tag.getString("cause"));
        } catch (Exception ignored) {}
        int id = tag.getInt("source");
        IAbstractChangedEntity source = null;
        if (id != -1 && level != null && level.getEntity(id) instanceof LivingEntity livingEntity)
            source = IAbstractChangedEntity.forEither(livingEntity);
        return new TransfurContext(cause, source);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("cause", cause.name());
        tag.putInt("source", source == null ? -1 : source.getEntity().getId());
        return tag;
    }
}
