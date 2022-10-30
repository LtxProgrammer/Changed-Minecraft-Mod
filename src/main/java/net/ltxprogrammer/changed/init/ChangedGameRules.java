package net.ltxprogrammer.changed.init;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.level.GameRules.register;

@Mod.EventBusSubscriber
public class ChangedGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEP_BRAIN = register("changed:keepConscious", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_LATEX_SPREAD = register("changed:latexSpread", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEP_FORM = register("changed:keepForm", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_TRANSFUR_TOLERANCE = register("changed:transfurTolerance", GameRules.Category.PLAYER, GameRules.IntegerValue.create(3000));
}
