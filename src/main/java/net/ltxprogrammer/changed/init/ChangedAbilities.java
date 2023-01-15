package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.ability.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ChangedAbilities {
    private static final Map<ResourceLocation, AbstractAbility<?>> REGISTRY = new HashMap<>();

    public static ExtraHandsAbility EXTRA_HANDS = register(new ExtraHandsAbility());
    public static AccessSaddleAbility ACCESS_SADDLE = register(new AccessSaddleAbility());
    public static SwitchTransfurModeAbility SWITCH_TRANSFUR_MODE = register(new SwitchTransfurModeAbility());
    public static CreateCobwebAbility CREATE_COBWEB = register(new CreateCobwebAbility());
    public static CreateInkballAbility CREATE_INKBALL = register(new CreateInkballAbility());
    public static SwitchHandsAbility SWITCH_HANDS = register(new SwitchHandsAbility());
    public static AccessChestAbility ACCESS_CHEST = register(new AccessChestAbility());

    public static <T extends AbstractAbility<?>> T register(T ability) {
        if (REGISTRY.containsKey(ability.getId()))
            throw new RuntimeException("Duplicate ability id");
        REGISTRY.put(ability.getId(), ability);
        return ability;
    }

    public static AbstractAbility<?> getAbility(ResourceLocation location) {
        return REGISTRY.get(location);
    }
}
