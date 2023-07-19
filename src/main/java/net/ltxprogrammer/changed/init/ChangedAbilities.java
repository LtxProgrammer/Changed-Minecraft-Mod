package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAbilities {
    public static final DeferredRegister<AbstractAbility<?>> REGISTRY = ChangedRegistry.ABILITY.createDeferred(Changed.MODID);

    public static RegistryObject<ExtraHandsAbility> EXTRA_HANDS = REGISTRY.register("extra_hands", ExtraHandsAbility::new);
    public static RegistryObject<AccessSaddleAbility> ACCESS_SADDLE = REGISTRY.register("access_saddle", AccessSaddleAbility::new);
    public static RegistryObject<SwitchTransfurModeAbility> SWITCH_TRANSFUR_MODE = REGISTRY.register("switch_transfur_mode", SwitchTransfurModeAbility::new);
    public static RegistryObject<SimpleCreateItemAbility> CREATE_COBWEB = REGISTRY.register("create_cobweb",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.COBWEB), 5.0f, 6.0f));
    public static RegistryObject<SimpleCreateItemAbility> CREATE_INKBALL = REGISTRY.register("create_inkball",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(ChangedItems.LATEX_INKBALL.get()), 5.0f, 6.0f));
    public static RegistryObject<SimpleCreateItemAbility> CREATE_HONEYCOMB = REGISTRY.register("create_honeycomb",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.HONEYCOMB), 5.0f, 6.0f));
    public static RegistryObject<SwitchHandsAbility> SWITCH_HANDS = REGISTRY.register("switch_hands", SwitchHandsAbility::new);
    public static RegistryObject<AccessChestAbility> ACCESS_CHEST = REGISTRY.register("access_chest", AccessChestAbility::new);
    public static RegistryObject<SwitchGenderAbility> SWITCH_GENDER = REGISTRY.register("switch_gender", SwitchGenderAbility::new);
    public static RegistryObject<UseVariantEffectAbility> USE_VARIANT_EFFECT = REGISTRY.register("use_variant_ability", UseVariantEffectAbility::new);
    public static RegistryObject<SlitherAbility> SLITHER = REGISTRY.register("slither", SlitherAbility::new);
    public static RegistryObject<SelectHairstyleAbility> SELECT_HAIRSTYLE = REGISTRY.register("select_hairstyle", SelectHairstyleAbility::new);
    public static RegistryObject<HoldEntityAbility> HOLD_ENTITY_ABILITY = REGISTRY.register("hold_entity", HoldEntityAbility::new);

    public static RegistryObject<SelectSpecialStateAbility> SELECT_SPECIAL_STATE = REGISTRY.register("select_special_state", SelectSpecialStateAbility::new);

    public static AbstractAbility<?> getAbility(ResourceLocation location) {
        return ChangedRegistry.ABILITY.get().getValue(location);
    }
}
