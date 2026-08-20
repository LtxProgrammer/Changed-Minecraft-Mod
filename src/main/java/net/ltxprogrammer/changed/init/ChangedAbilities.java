package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.active.*;
import net.ltxprogrammer.changed.ability.active.flying.*;
import net.ltxprogrammer.changed.ability.active.mer.*;
import net.ltxprogrammer.changed.ability.active.multiarm.*;
import net.ltxprogrammer.changed.ability.active.spider.*;
import net.ltxprogrammer.changed.ability.active.taur.BackKick;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAbilities {
    public static final DeferredRegister<AbstractAbility<?>> REGISTRY = ChangedRegistry.ABILITY.createDeferred(Changed.MODID);

    public static RegistryObject<SwitchTransfurModeAbility> SWITCH_TRANSFUR_MODE = REGISTRY.register("switch_transfur_mode", SwitchTransfurModeAbility::new);
    public static RegistryObject<SimpleCreateItemAbility> CREATE_COBWEB = REGISTRY.register("create_cobweb",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.COBWEB), 5.0f, 6.0f));
    public static RegistryObject<SimpleCreateItemAbility> CREATE_STRING = REGISTRY.register("create_string",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.STRING, 2), 5.0f, 6.0f));
    public static RegistryObject<SimpleCreateItemAbility> CREATE_INKBALL = REGISTRY.register("create_inkball",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(ChangedItems.LATEX_INKBALL.get()), 5.0f, 6.0f));
    public static RegistryObject<SimpleCreateItemAbility> CREATE_HONEYCOMB = REGISTRY.register("create_honeycomb",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.HONEYCOMB), 5.0f, 6.0f));
    public static RegistryObject<SimpleCreateItemAbility> CREATE_HONEY = REGISTRY.register("create_honey",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.HONEY_BOTTLE), 5.0f, 6.0f));
    public static RegistryObject<SwitchHandsAbility> SWITCH_HANDS = REGISTRY.register("switch_hands", SwitchHandsAbility::new);
    public static RegistryObject<AccessChestAbility> ACCESS_CHEST = REGISTRY.register("access_chest", AccessChestAbility::new);
    public static RegistryObject<SwitchGenderAbility> SWITCH_GENDER = REGISTRY.register("switch_gender", SwitchGenderAbility::new);
    public static RegistryObject<SlitherAbility> SLITHER = REGISTRY.register("slither", SlitherAbility::new);
    public static RegistryObject<SummonSharksAbility> SUMMON_SHARKS = REGISTRY.register("summon_sharks", SummonSharksAbility::new);
    public static RegistryObject<HypnosisAbility> HYPNOSIS = REGISTRY.register("hypnosis", HypnosisAbility::new);
    public static RegistryObject<SirenSingAbility> SIREN_SING = REGISTRY.register("siren_sing", SirenSingAbility::new);
    public static RegistryObject<PuddleAbility> PUDDLE = REGISTRY.register("puddle", PuddleAbility::new);
    public static RegistryObject<GrabEntityAbility> GRAB_ENTITY_ABILITY = REGISTRY.register("grab_entity", GrabEntityAbility::new);
    public static RegistryObject<FriendlyTransfurAbility> FRIENDLY_TRANSFUR = REGISTRY.register("friendly_transfur", FriendlyTransfurAbility::new);
    public static RegistryObject<ToggleNightVisionAbility> TOGGLE_NIGHT_VISION = REGISTRY.register("toggle_night_vision", ToggleNightVisionAbility::new);
    public static RegistryObject<ToggleWaveVisionAbility> TOGGLE_WAVE_VISION = REGISTRY.register("toggle_wave_vision", ToggleWaveVisionAbility::new);
    public static RegistryObject<UnderwaterDashAbility> UNDERWATER_DASH = REGISTRY.register("underwater_dash", UnderwaterDashAbility::new);
    public static RegistryObject<WingFlapAbility> WING_FLAP = REGISTRY.register("wing_flap", WingFlapAbility::new);
    public static RegistryObject<SkyDiveAbility> SKY_DIVE = REGISTRY.register("sky_dive", SkyDiveAbility::new);
    public static RegistryObject<GaleForceWindsAbility> GALE_FORCE_WINDS = REGISTRY.register("gale_force_winds", GaleForceWindsAbility::new);
    public static RegistryObject<WallClimbAbility> WALL_CLIMB = REGISTRY.register("wall_climb", WallClimbAbility::new);
    public static RegistryObject<CobwebRappelAbility> COBWEB_RAPPEL = REGISTRY.register("cobweb_rappel", CobwebRappelAbility::new);
    public static RegistryObject<InkSmokeScreenAbility> INK_SMOKE_SCREEN = REGISTRY.register("ink_smoke_screen", InkSmokeScreenAbility::new);
    public static RegistryObject<AutotoolAbility> AUTOTOOL = REGISTRY.register("autotool", AutotoolAbility::new);
    public static RegistryObject<ExcavateAbility> EXCAVATE = REGISTRY.register("excavate", ExcavateAbility::new);
    public static RegistryObject<MantleAbility> MANTLE = REGISTRY.register("mantle", MantleAbility::new);
    public static RegistryObject<HighJumpAbility> HIGH_JUMP = REGISTRY.register("high_jump", HighJumpAbility::new);
    public static RegistryObject<CamouflageAbility> CAMOUFLAGE = REGISTRY.register("camouflage", CamouflageAbility::new);
    public static RegistryObject<TailWhip> MER_TAIL_WHIP = REGISTRY.register("mer/tail_whip", TailWhip::new);
    public static RegistryObject<BackKick> TAUR_BACK_KICK = REGISTRY.register("taur/back_kick", BackKick::new);

    public static AbstractAbility<?> getAbility(ResourceLocation location) {
        return ChangedRegistry.ABILITY.get().getValue(location);
    }
}
