package net.ltxprogrammer.changed.ability.tree;

import net.ltxprogrammer.changed.ChangedConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/// What happens to the player's ability tree when they lose their variant
public record AbilityRegressConfig(int pointsLost, int levelsLost, int nodesLost,
                                   RemovalDirection removalDirection) {

    public enum RemovalDirection {
        LEAST_TO_MOST_EXPENSIVE,
        MOST_TO_LEAST_EXPENSIVE
    }

    public static final AbilityRegressConfig NO_REGRESSION = new AbilityRegressConfig(0, 0, 0, RemovalDirection.LEAST_TO_MOST_EXPENSIVE);
    public static final AbilityRegressConfig DEFAULT = new AbilityRegressConfig(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, RemovalDirection.LEAST_TO_MOST_EXPENSIVE);
    public static final AbilityRegressConfig FULL_REGRESSION = new AbilityRegressConfig(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, RemovalDirection.LEAST_TO_MOST_EXPENSIVE);

    public static ChangedConfig.WrappedConfig<AbilityRegressConfig> createConfig(ForgeConfigSpec.Builder builder, String prefix, AbilityRegressConfig defaultValue) {
        builder.comment("How many points the player loses when their ability tree regresses.");
        ForgeConfigSpec.IntValue pointsLostConfig = builder.defineInRange(prefix + ".pointsLost", defaultValue.pointsLost, 0, Integer.MAX_VALUE);
        builder.comment("How many levels the player loses when their ability tree regresses.");
        ForgeConfigSpec.IntValue levelsLostConfig = builder.defineInRange(prefix + ".levelsLost", defaultValue.levelsLost, 0, Integer.MAX_VALUE);
        builder.comment("How many purchased nodes the player loses when their ability tree regresses.");
        ForgeConfigSpec.IntValue nodesLostConfig = builder.defineInRange(prefix + ".nodesLost", defaultValue.nodesLost, 0, Integer.MAX_VALUE);
        builder.comment("How nodes are prioritized when removing purchased nodes.");
        ForgeConfigSpec.EnumValue<RemovalDirection> removalDirectionConfig = builder.defineEnum(prefix + ".nodeRemovalDirection", defaultValue.removalDirection);

        return new ChangedConfig.WrappedConfig<>() {
            @Override
            public AbilityRegressConfig getDefault() {
                return defaultValue;
            }

            @Override
            public void save() {
                pointsLostConfig.save();
                levelsLostConfig.save();
                nodesLostConfig.save();
                removalDirectionConfig.save();
            }

            @Override
            public void setValue(AbilityRegressConfig value) {
                pointsLostConfig.set(value.pointsLost);
                levelsLostConfig.set(value.levelsLost);
                nodesLostConfig.set(value.nodesLost);
                removalDirectionConfig.set(value.removalDirection);
            }

            @Override
            public AbilityRegressConfig get() {
                return new AbilityRegressConfig(
                        pointsLostConfig.get(),
                        levelsLostConfig.get(),
                        nodesLostConfig.get(),
                        removalDirectionConfig.get()
                );
            }
        };
    }
}
