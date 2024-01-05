package net.ltxprogrammer.changed.init;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.level.GameRules.register;

@Mod.EventBusSubscriber
public class ChangedGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEP_BRAIN = register("changed:keepConscious", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_PALE = register("changed:doPale", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEP_FORM = register("changed:keepForm", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_NPC_WANT_FUSE_PLAYER = register("changed:npcWantFusePlayer", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_LATEX_GROWTH_RATE = register("changed:latexGrowthRate", GameRules.Category.UPDATES, GameRules.IntegerValue.create(100));
    public static final GameRules.Key<GameRules.IntegerValue> RULE_FUSABILITY_DURATION_PLAYER = register("changed:fusabilityDurationPlayer", GameRules.Category.PLAYER, GameRules.IntegerValue.create(6000));
}
