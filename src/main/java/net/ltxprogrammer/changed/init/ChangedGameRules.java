package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.util.SimpleRegistryHolder;
import net.minecraft.world.level.GameRules;

public class ChangedGameRules {
    private static <T extends GameRules.Value<T>> SimpleRegistryHolder<GameRules.Key<T>> createHolder(String id, GameRules.Category category, GameRules.Type<T> type) {
        return new SimpleRegistryHolder<>(() -> GameRules.register(id, category, type));
    }

    public static final SimpleRegistryHolder<GameRules.Key<GameRules.BooleanValue>> RULE_KEEP_BRAIN = createHolder("changed:keepConscious", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final SimpleRegistryHolder<GameRules.Key<GameRules.BooleanValue>> RULE_DO_PALE = createHolder("changed:doPale", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false));
    public static final SimpleRegistryHolder<GameRules.Key<GameRules.BooleanValue>> RULE_KEEP_FORM = createHolder("changed:keepForm", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final SimpleRegistryHolder<GameRules.Key<GameRules.BooleanValue>> RULE_NPC_WANT_FUSE_PLAYER = createHolder("changed:npcWantFusePlayer", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final SimpleRegistryHolder<GameRules.Key<GameRules.BooleanValue>> RULE_DO_TRANSFUR_ANIMATION = createHolder("changed:doTransfurAnimation", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));

    public static final SimpleRegistryHolder<GameRules.Key<GameRules.IntegerValue>> RULE_LATEX_GROWTH_RATE = createHolder("changed:latexGrowthRate", GameRules.Category.UPDATES, GameRules.IntegerValue.create(100));
    public static final SimpleRegistryHolder<GameRules.Key<GameRules.IntegerValue>> RULE_FUSABILITY_DURATION_PLAYER = createHolder("changed:fusabilityDurationPlayer", GameRules.Category.PLAYER, GameRules.IntegerValue.create(6000));

    public static void registerGameRules() {
        RULE_KEEP_BRAIN.register();
        RULE_DO_PALE.register();
        RULE_KEEP_FORM.register();
        RULE_NPC_WANT_FUSE_PLAYER.register();
        RULE_DO_TRANSFUR_ANIMATION.register();
        RULE_LATEX_GROWTH_RATE.register();
        RULE_FUSABILITY_DURATION_PLAYER.register();
    }
}
