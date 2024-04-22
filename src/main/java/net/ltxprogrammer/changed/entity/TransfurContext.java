package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class TransfurContext {
    public final TransfurCause cause;
    public final @Nullable IAbstractChangedEntity source;

    public TransfurContext(TransfurCause cause, @Nullable IAbstractChangedEntity source) {
        this.cause = cause;
        this.source = source;
    }

    public static TransfurContext playerLatexAttack(Player player) {
        return new TransfurContext(
                player.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, IAbstractChangedEntity.forPlayer(player)
        );
    }

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
}
