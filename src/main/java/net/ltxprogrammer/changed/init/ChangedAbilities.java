package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AccessSaddleAbility;
import net.ltxprogrammer.changed.ability.ExtraHandsAbility;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ChangedAbilities {
    private static final Map<ResourceLocation, AbstractAbility> REGISTRY = new HashMap<>();

    public static ExtraHandsAbility EXTRA_HANDS = register(new ExtraHandsAbility());
    public static AccessSaddleAbility ACCESS_SADDLE = register(new AccessSaddleAbility());

    public static <T extends AbstractAbility> T register(T ability) {
        REGISTRY.put(ability.getId(), ability);
        return ability;
    }

    public static AbstractAbility getAbility(ResourceLocation location) {
        return REGISTRY.get(location);
    }
}
