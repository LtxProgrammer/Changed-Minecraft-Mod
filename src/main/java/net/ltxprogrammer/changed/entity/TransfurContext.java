package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class TransfurContext {
    public final TransfurCause cause;
    public final @Nullable IAbstractLatex source;

    public TransfurContext(TransfurCause cause, @Nullable IAbstractLatex source) {
        this.cause = cause;
        this.source = source;
    }

    public static TransfurContext playerLatexAttack(Player player) {
        return new TransfurContext(
                player.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, IAbstractLatex.forPlayer(player)
        );
    }

    public static TransfurContext npcLatexAttack(LatexEntity latex) {
        return new TransfurContext(
                latex.getRandom().nextBoolean() ? TransfurCause.ATTACK_REPLICATE_LEFT : TransfurCause.ATTACK_REPLICATE_RIGHT, IAbstractLatex.forLatex(latex)
        );
    }

    public static TransfurContext hazard(TransfurCause cause) {
        return new TransfurContext(cause, null);
    }
}
